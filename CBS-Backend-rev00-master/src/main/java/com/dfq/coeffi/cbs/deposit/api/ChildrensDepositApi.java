package com.dfq.coeffi.cbs.deposit.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLog;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.Dto.DepositApprovalDto;
import com.dfq.coeffi.cbs.deposit.Dto.DepositDto;
import com.dfq.coeffi.cbs.deposit.Dto.DepositNomineeDto;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.deposit.service.*;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormat;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormatType;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountFormatService;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.MemberType;
import com.dfq.coeffi.cbs.member.entity.NumberFormat;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.report.depositReports.DepositReportDto;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.dfq.coeffi.cbs.utils.ReportDownload;
import com.dfq.coeffi.cbs.utils.TransactionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.CREDIT;
import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.DEBIT;
import static com.dfq.coeffi.cbs.utils.DateUtil.*;
import static com.dfq.coeffi.cbs.utils.DepositInterestCalculation.calculateInterest;
import static com.dfq.coeffi.cbs.utils.DepositInterestCalculation.calculateMaturityAmount;

@Slf4j
@RestController
public class ChildrensDepositApi extends BaseController {

    private final ChildrensDepositService childrensDepositService;
    private final DepositsApprovalService depositsApprovalService;
    private final MemberService memberService;
    private final FixedDepositService fixedDepositService;
    private final DoubleSchemeService doubleSchemeService;
    private final RecurringDepositService recurringDepositService;
    private final TermDepositService termDepositService;
    private final PigmyDepositService pigmyDepositService;
    private final ApplicationLogService applicationLogService;
    private final DepositTransactionService depositTransactionService;
    private final DepositAccountNumberMasterService depositAccountNumberMasterService;
    private final TransactionService transactionService;
    private final AccountHeadService accountHeadService;
    private final BODDateService bodDateService;
    private final SavingsBankDepositService savingsBankDepositService;
    public final BankService bankService;
    public final SavingBankTransactionService savingBankTransactionService;
    public final AccountFormatService accountFormatService;
    long defaultApplicationNumber = 201900000;

    private ChildrensDepositApi(final ChildrensDepositService childrensDepositService, DepositsApprovalService depositsApprovalService,
                                MemberService memberService, final FixedDepositService fixedDepositService,
                                final DoubleSchemeService doubleSchemeService, final RecurringDepositService recurringDepositService,
                                final TermDepositService termDepositService, final PigmyDepositService pigmyDepositService,
                                final DepositTransactionService depositTransactionService, final DepositAccountNumberMasterService depositAccountNumberMasterService,
                                final TransactionService transactionService, final AccountHeadService accountHeadService, final BODDateService bodDateService,
                                final SavingsBankDepositService savingsBankDepositService, final ApplicationLogService applicationLogService,
                                final BankService bankService, SavingBankTransactionService savingBankTransactionService,
                                final AccountFormatService accountFormatService) {
        this.childrensDepositService = childrensDepositService;
        this.depositsApprovalService = depositsApprovalService;
        this.memberService = memberService;
        this.fixedDepositService = fixedDepositService;
        this.doubleSchemeService = doubleSchemeService;
        this.recurringDepositService = recurringDepositService;
        this.termDepositService = termDepositService;
        this.pigmyDepositService = pigmyDepositService;
        this.depositTransactionService = depositTransactionService;
        this.depositAccountNumberMasterService = depositAccountNumberMasterService;
        this.transactionService = transactionService;
        this.accountHeadService = accountHeadService;
        this.bodDateService = bodDateService;
        this.savingsBankDepositService = savingsBankDepositService;
        this.applicationLogService = applicationLogService;
        this.bankService = bankService;
        this.savingBankTransactionService = savingBankTransactionService;
        this.accountFormatService = accountFormatService;
    }

    @PostMapping("child-deposit/unapprove-list")
    public ResponseEntity<List<ChildrensDeposit>> getAllDeposits(@RequestBody DepositApprovalDto depositApprovalDto) {
        List<ChildrensDeposit> allDeposits = childrensDepositService.getAllDeposits(depositApprovalDto.dateFrom, depositApprovalDto.dateTo);
        if (CollectionUtils.isEmpty(allDeposits)) {
            throw new EntityNotFoundException("allDeposits");
        }
        return new ResponseEntity<>(allDeposits, HttpStatus.OK);
    }

    @PostMapping("child-deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ChildrensDeposit> createDeposit(@Valid @RequestBody final ChildrensDeposit childrensDeposit, Principal principal) {
        bodDateService.checkBOD();

        Ledger ledger = accountHeadService.getLedgerByName("Children Deposit");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head and ledger not found for children deposit");
        }

        AccountFormat accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.DEPOSIT, "CHILDREN_DEPOSIT");
        if (accountFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate account number for children deposit");
        }
        String accountNumber = accountFormat.getPrefix() + "-" + (accountFormat.getFromAccountNumber() + 1);

        NumberFormat numberFormat = memberService.getNumberFormatByType("Deposit_Receipt_Number");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate receipt number for children deposit");
        }
        String receiptNumber = numberFormat.getPrefix() + "-" + (numberFormat.getReceiptNumber() + 1);

        Optional<Member> memberObj = memberService.getMember(childrensDeposit.getMember().getId());
        if (!memberObj.isPresent()) {
            log.warn("Unable to find member with ID : {} not found", childrensDeposit.getMember().getId());
            throw new EntityNotFoundException(Member.class.getName());
        }
        Member member = memberObj.get();

        if (member.getMemberType() == MemberType.NOMINAL) {
            throw new EntityNotFoundException("Selected Member is nominal member, can not open Children Deposit");
        }

        childrensDeposit.setMember(member);
        savingsBankDepositService.checkSavingBankAccount(member); //check SB/CA Account

//        if(member.getOrganisationName().equalsIgnoreCase("Individual")){
//        }
        BigDecimal interest = calculateInterest(childrensDeposit.getDepositAmount(), childrensDeposit.getPeriodOfDeposit(), childrensDeposit.getRateOfInterest());
        BigDecimal maturityAmount = calculateMaturityAmount(childrensDeposit.getDepositAmount(), interest);
        childrensDeposit.setInterestAmount(interest);
        childrensDeposit.setMaturityAmount(maturityAmount);
        childrensDeposit.setWithDrawn(false);
        childrensDeposit.setVoucherType("RECEIPT");
        childrensDeposit.setTransactionType("CREDIT");
        Optional<DepositAccountNumberMaster> accountNumberMaster = depositAccountNumberMasterService.getDepositAccountNumberMasterById(1);
        childrensDeposit.setApplicationNumber(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberCD()) + defaultApplicationNumber));
        accountNumberMaster.get().setAccountNumberCD(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberCD()) + 1));
        depositAccountNumberMasterService.saveDepositAccountNumberMaster(accountNumberMaster.get());
        childrensDeposit.setMaturityDate(DateUtil.addYearsToDate(childrensDeposit.getPeriodOfDeposit().intValue()));
        childrensDeposit.setTransactionBy(getLoggedUser(principal));
        childrensDeposit.setAccountHead(ledger.getAccountHead());
        childrensDeposit.setLedger(ledger);

        childrensDeposit.setAccountNumber(accountNumber);
        childrensDeposit.setReceiptNumber(receiptNumber);

        ChildrensDeposit persistedDeposit = childrensDepositService.saveDeposit(childrensDeposit);
        if (persistedDeposit != null) {
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "CHILDRENS DEPOSIT CREATED", "CHILDRENS DEPOSIT POSTED", loggedUser.getId());
            accountFormat.setFromAccountNumber(accountFormat.getFromAccountNumber() + 1);
            accountFormatService.saveAccountNumber(accountFormat);

            numberFormat.setReceiptNumber(numberFormat.getReceiptNumber() + 1);
            memberService.updateNumberFormat(numberFormat);
        }
        return new ResponseEntity<>(persistedDeposit, HttpStatus.CREATED);
    }

    public BigDecimal calculateInterest(BigDecimal depositAmount, BigDecimal periodOfDeposit, double rateOfInterest) {
        BigDecimal rate = new BigDecimal(rateOfInterest);

        BigDecimal percent = new BigDecimal(100);
        if (depositAmount == null || rate == null || rateOfInterest == 0) {
            throw new EntityNotFoundException("DepositAmount or Period or Interest rate not found");
        }
        BigDecimal interest = depositAmount.multiply(periodOfDeposit.multiply(rate)).divide(percent);

        return interest;
    }

    public BigDecimal calculateMaturityAmount(BigDecimal depositAmount, BigDecimal interestAmount) {
        BigDecimal maturityAmount = depositAmount.add(interestAmount);
        return maturityAmount;
    }

    @PutMapping("child-deposit/{id}")
    public ResponseEntity<ChildrensDeposit> updateDeposit(@PathVariable long id, @Valid @RequestBody ChildrensDeposit deposit, Principal principal) {
        Optional<ChildrensDeposit> persistedDeposit = childrensDepositService.getDepositById(id);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException(ChildrensDeposit.class.getSimpleName());
        }
        deposit.setId(id);
        deposit.setCreatedOn(persistedDeposit.get().getCreatedOn());
        ChildrensDeposit childObj = childrensDepositService.saveDeposit(deposit);
        if (childObj != null) {
            User loggedUser = getLoggedUser(principal);
            ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "CHILDRENS DEPOSIT MODIFIED", "CHILDRENS DEPOSIT MODIFIED", loggedUser.getId());
        }
        return new ResponseEntity<>(deposit, HttpStatus.OK);
    }

    @PostMapping("CHILDRENS_DEPOSIT/add-nominee/{id}")
    public ResponseEntity<ChildrensDeposit> addNomineeDetails(@PathVariable long id, @Valid @RequestBody DepositNomineeDto depositNomineeDto, Principal principal) {
        Optional<ChildrensDeposit> persistedDeposit = childrensDepositService.getDepositById(id);
        ChildrensDeposit childrensDeposit = persistedDeposit.get();
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException(ChildrensDeposit.class.getSimpleName());
        }
        if (childrensDeposit.getDepositNomineeDetailsTwo() == null) {
            childrensDeposit.setDepositNomineeDetailsTwo(depositNomineeDto.getDepositNomineeDetailsTwo());
        } else if (childrensDeposit.getDepositNomineeDetailsThree() == null) {
            childrensDeposit.setDepositNomineeDetailsThree(depositNomineeDto.getDepositNomineeDetailsThree());
        }
        ChildrensDeposit childObj = childrensDepositService.saveDeposit(childrensDeposit);
        return new ResponseEntity<>(childrensDeposit, HttpStatus.OK);
    }

    @DeleteMapping("child-deposit/{id}")
    public ResponseEntity<ChildrensDeposit> deleteDeposit(@PathVariable Long id) {
        Optional<ChildrensDeposit> deposit = childrensDepositService.getDepositById(id);
        if (!deposit.isPresent()) {
            throw new EntityNotFoundException(ChildrensDeposit.class.getName());
        }
        childrensDepositService.deleteDeposit(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("child-deposit/approval")
    public ResponseEntity<ChildrensDeposit> childrenDepositApproval(@RequestBody DepositApprovalDto depositApprovalDto, Principal principal) {
        DepositsApproval depositsApproval = saveDepositsApproval(principal, depositApprovalDto);
        Optional<ChildrensDeposit> persistedChildrensDeposit = null;
        for (long id : depositApprovalDto.ids) {
            persistedChildrensDeposit = childrensDepositService.getDepositById(id);
            if (!persistedChildrensDeposit.isPresent()) {
                throw new EntityNotFoundException(FixedDeposit.class.getSimpleName());
            }
            persistedChildrensDeposit.get().setDepositsApproval(depositsApproval);
            persistedChildrensDeposit.get().setStatus(true);
            persistedChildrensDeposit.get().setTransactionBy(getLoggedUser(principal));
            ChildrensDeposit childObj = childrensDepositService.saveDeposit(persistedChildrensDeposit.get());
            saveDepositTransaction(persistedChildrensDeposit.get());
            if (childObj != null) {
                transactionCreditEntry(childObj);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(childObj.getDepositAmount(), "CREDIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(getLoggedUser(principal).getFirstName(), "CHILDRENS DEPOSIT APPROVED", "DEPOSIT APPROVAL", loggedUser.getId());
            }
        }
        return new ResponseEntity<>(persistedChildrensDeposit.get(), HttpStatus.OK);
    }

    public DepositsApproval saveDepositsApproval(Principal principal, DepositApprovalDto depositApprovalDto) {
        DepositsApproval depositsApproval = new DepositsApproval();
        depositsApproval.setApproved(true);
        depositsApproval.setRemarks(depositApprovalDto.remarks);
        depositsApproval.setApprovedBy(getLoggedUser(principal).getFirstName());
        depositsApprovalService.saveDepositsApproval(depositsApproval);
        return depositsApproval;
    }

    @PostMapping("child-deposit/account-number")
    public ResponseEntity<ChildrensDeposit> getChildrensDepositByAccountNumber(@RequestBody DepositReportDto depositReportDto) {
        Optional<ChildrensDeposit> persistedDeposit = childrensDepositService.getChildrenDepositByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("No Active Deposits found for the account number: " + depositReportDto.accountNumber);
        }
        return new ResponseEntity(persistedDeposit.get(), HttpStatus.OK);
    }

    @PostMapping("/CHILDRENS_DEPOSIT/refund")
    public ResponseEntity<ChildrensDeposit> refundChildDeposit(@RequestBody DepositReportDto depositReportDto, Principal principal) {
        bodDateService.checkBOD();

        Optional<ChildrensDeposit> persistedDeposit = childrensDepositService.getChildrenDepositByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("ChildrensDeposit not found or not approved");
        }
        ChildrensDeposit childrensDeposit = persistedDeposit.get();
        childrensDeposit.setId(childrensDeposit.getId());
        childrensDeposit.setWithDrawn(true);
        childrensDeposit.setAccountClosedOn(getTodayDate());
        childrensDeposit.setModeOfPayment(depositReportDto.getModeOfPayment());
        ChildrensDeposit persisted = childrensDepositService.saveDeposit(childrensDeposit);
        if (depositReportDto.getModeOfPayment().equalsIgnoreCase("Cash")) {
            if (persisted != null) {
                transactionDebitEntry(persisted);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(persisted.getMaturityAmount(), "DEBIT");
            }
        } else if (depositReportDto.getModeOfPayment().equalsIgnoreCase("Bank")) {
            if (persisted != null) {
                transactionDebitEntry(persisted);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(persisted.getMaturityAmount(), "DEBIT");
            }
        } else if (depositReportDto.getModeOfPayment().equalsIgnoreCase("Transfer")) {
            if (persisted != null) {
                User loggedUser = getLoggedUser(principal);
                transactionDebitEntry(persisted);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(persisted.getMaturityAmount(), "DEBIT");
                depositRefundCreditTransactionEntry(persisted, loggedUser);
            }
        }
        if (persisted != null) {
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Children deposit refunded Account number" + persisted.getAccountNumber(), "CHILDRENS DEPOSIT POSTED", loggedUser.getId());
        }
        withDraw(childrensDeposit);
        return new ResponseEntity<>(childrensDeposit, HttpStatus.OK);
    }

    public DepositTransaction withDraw(ChildrensDeposit childrensDeposit) {
        DepositTransaction depositTransaction = new DepositTransaction();
        depositTransaction.setAccountNumber(childrensDeposit.getAccountNumber());
        depositTransaction.setDepositType(childrensDeposit.getDepositType());
        depositTransaction.setMember(childrensDeposit.getMember());
        depositTransaction.setTransactionType(TransactionType.DEBIT);
        depositTransaction.setWithDrawAmount(childrensDeposit.getMaturityAmount());
        depositTransactionService.createDepositTransactionService(depositTransaction);
        return depositTransaction;
    }

    public DepositTransaction saveDepositTransaction(ChildrensDeposit childrensDeposit) {
        DepositTransaction depositTransaction = new DepositTransaction();
        depositTransaction.setAccountNumber(childrensDeposit.getAccountNumber());
        depositTransaction.setDepositType(childrensDeposit.getDepositType());
        depositTransaction.setDepositAmount(childrensDeposit.getDepositAmount());
        depositTransaction.setInterestAmount(childrensDeposit.getInterestAmount());
        depositTransaction.setMember(childrensDeposit.getMember());
        depositTransaction.setTransactionType(TransactionType.CREDIT);
        depositTransaction.setBalanceAmount(childrensDeposit.getMaturityAmount());
        depositTransactionService.createDepositTransactionService(depositTransaction);
        return depositTransaction;
    }

    @PostMapping("child-deposit/calculation")
    public ArrayList getCalcData(@RequestBody ChildrensDeposit childrensDeposit) {
        ArrayList al = new ArrayList();
        BigDecimal interest = BigDecimal.ZERO;
        BigDecimal maturityAmount = BigDecimal.ZERO;
        Date date = null;
        if(childrensDeposit.getPeriodOfName().equalsIgnoreCase("Yearly")){
            date = DateUtil.addYearsToDate(childrensDeposit.getPeriodOfDeposit().intValue());
            interest = calculateInterest(childrensDeposit.getDepositAmount(), childrensDeposit.getPeriodOfDeposit(), childrensDeposit.getRateOfInterest());
            maturityAmount = calculateMaturityAmount(childrensDeposit.getDepositAmount(), interest);
        } else if(childrensDeposit.getPeriodOfName().equalsIgnoreCase("Monthly")){
            date = DateUtil.addMonthsToDate(childrensDeposit.getPeriodOfDeposit().intValue());
            BigDecimal convertToYear = BigDecimal.valueOf(childrensDeposit.getPeriodOfDeposit().doubleValue()/12);
            interest = calculateInterest(childrensDeposit.getDepositAmount(), convertToYear, childrensDeposit.getRateOfInterest());
            maturityAmount = calculateMaturityAmount(childrensDeposit.getDepositAmount(), interest);
        }
/*        BigDecimal interest = calculateInterest(childrensDeposit.getDepositAmount(), childrensDeposit.getPeriodOfDeposit(), childrensDeposit.getRateOfInterest());
        BigDecimal maturityAmount = calculateMaturityAmount(childrensDeposit.getDepositAmount(), interest);
        Date maturityDate = DateUtil.addYearsToDate(childrensDeposit.getPeriodOfDeposit().intValue());*/
        al.add(maturityAmount);
        al.add(interest);
        al.add(DateUtil.convertToDateString(date));
        return al;
    }

    @GetMapping("all-deposite/{memberNumber}")
    public ResponseEntity<List<DepositDto>> getAllDepositsByMemberNumber(@PathVariable String memberNumber) {

        System.out.println("Calling : " + memberNumber);

        List<PigmyDeposit> pigmyDeposit = null;

        List<ChildrensDeposit> childrensDeposits = childrensDepositService.getChildrenDepositByMemberNumber(memberNumber);
        List<DoubleScheme> doubleSchemes = doubleSchemeService.getDoubleSchemeDepositByMemberNumber(memberNumber);
        List<FixedDeposit> fixedDeposits = fixedDepositService.getFixedDepositByMemberNumber(memberNumber);
        List<RecurringDeposit> recurringDeposits = recurringDepositService.getRecuringDepositByMemberNumber(memberNumber);
        List<TermDeposit> termDeposits = termDepositService.getTermDepositByMemberNumber(memberNumber);
        List<PigmyDeposit> pigmyDeposits = pigmyDepositService.getPigmyDepositByMemberNumber(memberNumber);

        Optional<Member> memberObj = memberService.findMemberByMemberNumber(memberNumber);
        if (memberObj.isPresent()) {
            Member member = memberObj.get();
            pigmyDeposit = pigmyDepositService.getPigmyDepositByCustomerId(member.getCustomer().getId());
        }
        List<DepositDto> depositDtos = new ArrayList<>();

        if (childrensDeposits != null && childrensDeposits.size() > 0) {
            for (ChildrensDeposit childrensDeposit : childrensDeposits) {
                DepositDto depositDto = new DepositDto();
                depositDto.setMember(childrensDeposit.getMember());
                depositDto.setMemberId(childrensDeposit.getMember().getId());
                depositDto.setDepositType(childrensDeposit.getDepositType());
                depositDto.setDepositId(childrensDeposit.getId());
                depositDto.setMemberNumber(memberNumber);
                depositDto.setApplicationNumber(childrensDeposit.getApplicationNumber());
                depositDto.setTransactionNumber(childrensDeposit.getTransactionNumber());
                depositDto.setAccountNumber(childrensDeposit.getAccountNumber());
                depositDto.setExgAccountNumber(childrensDeposit.getExgAccountNumber());
                depositDto.setDepositAmount(childrensDeposit.getDepositAmount());
                depositDto.setPeriodOfDeposit(childrensDeposit.getPeriodOfDeposit());
                depositDto.setRateOfInterest(childrensDeposit.getRateOfInterest());
                depositDto.setMaturityAmount(childrensDeposit.getMaturityAmount());
                depositDto.setInterestAmount(childrensDeposit.getInterestAmount());
                depositDto.setMaturityDate(childrensDeposit.getMaturityDate());
                depositDto.setStatus(childrensDeposit.isStatus());
                depositDto.setWithDrawn(childrensDeposit.isWithDrawn());
                depositDto.setDepositsApproval(childrensDeposit.getDepositsApproval());
                depositDto.setDepositNomineeDetails(childrensDeposit.getDepositNomineeDetails());
                depositDto.setDepositNomineeDetailsTwo(childrensDeposit.getDepositNomineeDetailsTwo());
                depositDto.setDepositNomineeDetailsThree(childrensDeposit.getDepositNomineeDetailsThree());
                depositDto.setReceiptNumber(childrensDeposit.getReceiptNumber());
                depositDto.setCreatedOn(childrensDeposit.getCreatedOn());

                depositDtos.add(depositDto);
            }
        }

        if (doubleSchemes != null && doubleSchemes.size() > 0) {
            for (DoubleScheme doubleScheme : doubleSchemes) {
                DepositDto depositDto = new DepositDto();
                depositDto.setMember(doubleScheme.getMember());
                depositDto.setMemberId(doubleScheme.getMember().getId());
                depositDto.setDepositType(doubleScheme.getDepositType());
                depositDto.setDepositId(doubleScheme.getId());
                depositDto.setMemberNumber(memberNumber);
                depositDto.setApplicationNumber(doubleScheme.getApplicationNumber());
                depositDto.setTransactionNumber(doubleScheme.getTransactionNumber());
                depositDto.setAccountNumber(doubleScheme.getAccountNumber());
                depositDto.setDepositAmount(doubleScheme.getDepositAmount());
                depositDto.setPeriodOfDeposit(doubleScheme.getPeriodOfDeposit());
                depositDto.setRateOfInterest(doubleScheme.getRateOfInterest());
                depositDto.setMaturityAmount(doubleScheme.getMaturityAmount());
                depositDto.setInterestAmount(doubleScheme.getInterestAmount());
                depositDto.setMaturityDate(doubleScheme.getMaturityDate());
                depositDto.setStatus(doubleScheme.isStatus());
                depositDto.setWithDrawn(doubleScheme.isWithDrawn());
                depositDto.setDepositsApproval(doubleScheme.getDepositsApproval());
                depositDto.setDepositNomineeDetails(doubleScheme.getDepositNomineeDetails());
                depositDto.setDepositNomineeDetailsTwo(doubleScheme.getDepositNomineeDetailsTwo());
                depositDto.setDepositNomineeDetailsThree(doubleScheme.getDepositNomineeDetailsThree());
                depositDto.setReceiptNumber(doubleScheme.getReceiptNumber());
                depositDto.setCreatedOn(doubleScheme.getCreatedOn());

                depositDtos.add(depositDto);
            }
        }

        if (fixedDeposits != null && fixedDeposits.size() > 0) {
            for (FixedDeposit fixedDeposit : fixedDeposits) {
                DepositDto depositDto = new DepositDto();
                depositDto.setMember(fixedDeposit.getMember());
                depositDto.setMemberId(fixedDeposit.getMember().getId());
                depositDto.setDepositType(fixedDeposit.getDepositType());
                depositDto.setDepositId(fixedDeposit.getId());
                depositDto.setMemberNumber(memberNumber);
                depositDto.setApplicationNumber(fixedDeposit.getApplicationNumber());
                depositDto.setTransactionNumber(fixedDeposit.getTransactionNumber());
                depositDto.setAccountNumber(fixedDeposit.getAccountNumber());
                depositDto.setDepositAmount(fixedDeposit.getDepositAmount());
                depositDto.setPeriodOfDeposit(fixedDeposit.getPeriodOfDeposit());
                depositDto.setRateOfInterest(fixedDeposit.getRateOfInterest());
                depositDto.setMaturityAmount(fixedDeposit.getMaturityAmount());
                depositDto.setInterestAmount(fixedDeposit.getInterestAmount());
                depositDto.setMaturityDate(fixedDeposit.getMaturityDate());
                depositDto.setStatus(fixedDeposit.isStatus());
                depositDto.setWithDrawn(fixedDeposit.isWithDrawn());
                depositDto.setDepositsApproval(fixedDeposit.getDepositsApproval());
                depositDto.setDepositNomineeDetails(fixedDeposit.getDepositNomineeDetails());
                depositDto.setDepositNomineeDetailsTwo(fixedDeposit.getDepositNomineeDetailsTwo());
                depositDto.setDepositNomineeDetailsThree(fixedDeposit.getDepositNomineeDetailsThree());
                depositDto.setReceiptNumber(fixedDeposit.getReceiptNumber());
                depositDto.setCreatedOn(fixedDeposit.getCreatedOn());

                depositDtos.add(depositDto);
            }
        }

        if (recurringDeposits != null && recurringDeposits.size() > 0) {
            for (RecurringDeposit recurringDeposit : recurringDeposits) {
                DepositDto depositDto = new DepositDto();
                depositDto.setMember(recurringDeposit.getMember());
                depositDto.setMemberId(recurringDeposit.getMember().getId());
                depositDto.setDepositType(recurringDeposit.getDepositType());
                depositDto.setDepositId(recurringDeposit.getId());
                depositDto.setMemberNumber(memberNumber);
                depositDto.setApplicationNumber(recurringDeposit.getApplicationNumber());
                depositDto.setTransactionNumber(recurringDeposit.getTransactionNumber());
                depositDto.setAccountNumber(recurringDeposit.getAccountNumber());
                depositDto.setDepositAmount(recurringDeposit.getDepositAmount());
                depositDto.setRateOfInterest(recurringDeposit.getRateOfInterest());
                depositDto.setMaturityAmount(recurringDeposit.getMaturityAmount());
                depositDto.setInterestAmount(recurringDeposit.getInterestAmount());
                depositDto.setMaturityDate(recurringDeposit.getMaturityDate());
                depositDto.setStatus(recurringDeposit.isStatus());
                depositDto.setWithDrawn(recurringDeposit.isWithDrawn());
                depositDto.setBalance(recurringDeposit.getBalance());
                depositDto.setDepositsApproval(recurringDeposit.getDepositsApproval());
                depositDto.setDepositNomineeDetails(recurringDeposit.getDepositNomineeDetails());
                depositDto.setDepositNomineeDetailsTwo(recurringDeposit.getDepositNomineeDetailsTwo());
                depositDto.setDepositNomineeDetailsThree(recurringDeposit.getDepositNomineeDetailsThree());
                depositDto.setNumberOfInstallments(recurringDeposit.getNumberOfInstallments());
                depositDto.setReceiptNumber(recurringDeposit.getReceiptNumber());
                depositDto.setCreatedOn(recurringDeposit.getCreatedOn());

                depositDtos.add(depositDto);
            }
        }

        if (termDeposits != null && termDeposits.size() > 0) {
            for (TermDeposit termDeposit : termDeposits) {
                DepositDto depositDto = new DepositDto();
                depositDto.setMember(termDeposit.getMember());
                depositDto.setMemberId(termDeposit.getMember().getId());
                depositDto.setDepositType(termDeposit.getDepositType());
                depositDto.setDepositId(termDeposit.getId());
                depositDto.setMemberNumber(memberNumber);
                depositDto.setApplicationNumber(termDeposit.getApplicationNumber());
                depositDto.setTransactionNumber(termDeposit.getTransactionNumber());
                depositDto.setAccountNumber(termDeposit.getAccountNumber());
                depositDto.setDepositAmount(termDeposit.getDepositAmount());
                depositDto.setPeriodOfDeposit(termDeposit.getPeriodOfDeposit());
                depositDto.setRateOfInterest(termDeposit.getRateOfInterest());
                depositDto.setMaturityAmount(termDeposit.getMaturityAmount());
                depositDto.setInterestAmount(termDeposit.getInterestAmount());
                depositDto.setMaturityDate(termDeposit.getMaturityDate());
                depositDto.setStatus(termDeposit.isStatus());
                depositDto.setWithDrawn(termDeposit.isWithDrawn());
                depositDto.setDepositsApproval(termDeposit.getDepositsApproval());
                depositDto.setDepositNomineeDetails(termDeposit.getDepositNomineeDetails());
                depositDto.setDepositNomineeDetailsTwo(termDeposit.getDepositNomineeDetailsTwo());
                depositDto.setDepositNomineeDetailsThree(termDeposit.getDepositNomineeDetailsThree());
                depositDto.setReceiptNumber(termDeposit.getReceiptNumber());
                depositDto.setCreatedOn(termDeposit.getCreatedOn());

                depositDtos.add(depositDto);
            }
        }
        if (pigmyDeposits != null && pigmyDeposits.size() > 0) {
            for (PigmyDeposit pigmyDeposit1Obj : pigmyDeposits) {
                DepositDto depositDto = new DepositDto();
                depositDto.setMember(pigmyDeposit1Obj.getMember());
                depositDto.setMemberId(pigmyDeposit1Obj.getMember().getId());
                depositDto.setDepositType(pigmyDeposit1Obj.getDepositType());
                depositDto.setDepositId(pigmyDeposit1Obj.getId());
                depositDto.setMemberNumber(memberNumber);
                depositDto.setApplicationNumber(pigmyDeposit1Obj.getApplicationNumber());
                depositDto.setTransactionNumber(pigmyDeposit1Obj.getTransactionNumber());
                depositDto.setAccountNumber(pigmyDeposit1Obj.getAccountNumber());
                depositDto.setDepositAmount(pigmyDeposit1Obj.getDepositAmount());
                depositDto.setMaturityAmount(pigmyDeposit1Obj.getMaturityAmount());
                depositDto.setMaturityDate(pigmyDeposit1Obj.getMaturityDate());
                depositDto.setStatus(pigmyDeposit1Obj.isStatus());
                depositDto.setWithDrawn(pigmyDeposit1Obj.isWithDrawn());
                depositDto.setDepositsApproval(pigmyDeposit1Obj.getDepositsApproval());
                depositDto.setDepositNomineeDetails(pigmyDeposit1Obj.getDepositNomineeDetails());
                depositDto.setDepositNomineeDetailsTwo(pigmyDeposit1Obj.getDepositNomineeDetailsTwo());
                depositDto.setDepositNomineeDetailsThree(pigmyDeposit1Obj.getDepositNomineeDetailsThree());
                depositDto.setReceiptNumber(pigmyDeposit1Obj.getReceiptNumber());
                depositDto.setPeriodOfDeposit(pigmyDeposit1Obj.getPeriodOfDeposit());
                depositDto.setInterestAmount(pigmyDeposit1Obj.getCalculatedInterest());
                depositDto.setCreatedOn(pigmyDeposit1Obj.getCreatedOn());


                depositDtos.add(depositDto);
            }
        }
        if (CollectionUtils.isEmpty(depositDtos)) {
            throw new EntityNotFoundException("No deposits found");
        }
        return new ResponseEntity<>(depositDtos, HttpStatus.OK);
    }

    @GetMapping("children-deposit")
    public ResponseEntity<ChildrensDeposit> getChildrenDepositByMemberNumber() {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void transactionCreditEntry(ChildrensDeposit childrensDeposit) {

        Ledger ledger = accountHeadService.getLedgerByName("Children Deposit");
        if(ledger == null){
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Children Deposit Account " + childrensDeposit.getId());
            creditTransaction.setCreditAmount(childrensDeposit.getDepositAmount());
            creditTransaction.setTransactionBy(childrensDeposit.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setAccountName(childrensDeposit.getMember().getName() + " (" + childrensDeposit.getMember().getMemberNumber() + ")");

            creditTransaction.setTransactionBy(childrensDeposit.getTransactionBy());
            creditTransaction.setVoucherType(childrensDeposit.getVoucherType());
            creditTransaction.setDebitAmount(BigDecimal.ZERO);
            creditTransaction.setParticulars("CHILDRENS DEPOSIT");
            creditTransaction.setTransferType("Cash");
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName, CREDIT);

            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountNumber(childrensDeposit.getAccountNumber());
            Transaction persistedDebitTransaction = transactionService.transactionEntry(creditTransaction);
        }
    }

    private void transactionDebitEntry(ChildrensDeposit childrensDeposit) {

        Ledger ledger = accountHeadService.getLedgerByName("Children Deposit");
        if(ledger == null){
            throw new EntityNotFoundException("Account head or ledger not found");
        }
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(childrensDeposit.getMaturityAmount());
            debitTransaction.setRemark("Amount debited for ChildrensDeposit Withdrawn " + childrensDeposit.getId());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(childrensDeposit.getTransactionBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setParticulars("CHILDRENS DEPOSIT");
            debitTransaction.setAccountName(childrensDeposit.getMember().getName() + " (" + childrensDeposit.getMember().getMemberNumber() + ")");
            debitTransaction.setVoucherType(childrensDeposit.getVoucherType());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName, DEBIT);
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountNumber(childrensDeposit.getAccountNumber());
            debitTransaction.setTransactionBy(childrensDeposit.getTransactionBy());
            debitTransaction.setTransferType(childrensDeposit.getModeOfPayment());
            Transaction persistedDebitTransaction = transactionService.transactionEntry(debitTransaction);
        }
    }

    private void depositRefundCreditTransactionEntry(ChildrensDeposit childrensDeposit, User user) {
        Ledger ledger = null;

        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(childrensDeposit.getMember().getMemberNumber());

        if (savingsBankDeposit == null) {
            throw new EntityNotFoundException("Saving Bank Account Not Found ");
        }

        if(savingsBankDeposit.getAccountType().equals(AccountType.SAVING)){
            ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
        }else{
            ledger = accountHeadService.getLedgerByName("Current Account");
        }
        if(ledger == null){
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        SavingBankTransaction savingBankTransaction = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(savingsBankDeposit.getAccountNumber());

        if (savingBankTransaction != null) {

            BigDecimal depositBalance = savingBankTransaction.getBalance();
            depositBalance = depositBalance.add(childrensDeposit.getMaturityAmount());

            SavingBankTransaction sbTransaction = new SavingBankTransaction();

            sbTransaction.setTransactionType("CREDIT");
            sbTransaction.setCreditAmount(childrensDeposit.getMaturityAmount());

            sbTransaction.setBalance(depositBalance);
            sbTransaction.setDebitAmount(BigDecimal.ZERO);
            sbTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
            sbTransaction.setTransactionBy(user);
            sbTransaction.setSavingsBankDeposit(savingsBankDeposit);

            savingBankTransactionService.createSavingBankTransaction(sbTransaction);

            savingsBankDeposit.setBalance(depositBalance);

            savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

            if (savingBankTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credited to saving bank Acc No." + savingBankTransaction.getAccountNumber() + " submitted",
                        "SAVING BANK TRANSACTION", user.getId());
            }
            TransactionUtil transactionUtil = new TransactionUtil(bankService);
            transactionUtil.getUpdateSocietyBalance(childrensDeposit.getMaturityAmount(), "CREDIT");
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For debit transaction against the refund share
            Transaction creditTransaction = new Transaction();

            creditTransaction.setRemark("Amount credited to Saving Bank Acc No. : " + savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setCreditAmount(childrensDeposit.getMaturityAmount());
            creditTransaction.setAccountName(childrensDeposit.getMember().getName() + " (" + childrensDeposit.getMember().getMemberNumber() + ")");
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransferType(childrensDeposit.getModeOfPayment());
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(childrensDeposit.getMaturityAmount());
            creditTransaction.setBalance(balance);

            transactionService.transactionEntry(creditTransaction);

            if (creditTransaction != null) {
                String message = "" + creditTransaction.getCreditAmount() + " Amount credited to saving bank Acc No. : " + savingsBankDeposit.getAccountNumber() + "From : " + childrensDeposit.getMember().getName();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Children Deposit Module", user.getId());
            }
            if (creditTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credit from society share bank Acc No. " + creditTransaction.getId() + " submitted",
                        "CHILDREN DEPOSIT REFUNDED", user.getId());
            }
        }
    }

    @PostMapping("/CHILDRENS_DEPOSIT/refund/pre-mature")
    public ResponseEntity<ChildrensDeposit> refundPreMatureChildDeposit(@RequestBody DepositReportDto depositReportDto) throws ParseException {
        bodDateService.checkBOD();
        Optional<ChildrensDeposit> persistedDeposit = childrensDepositService.getChildrenDepositByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("ChildrensDeposit not found or not approved");
        }
        ChildrensDeposit childrensDeposit = persistedDeposit.get();
        ChildrensDeposit persisted = viewPrematureDetails(childrensDeposit);
        return new ResponseEntity<>(persisted, HttpStatus.OK);
    }

    private ChildrensDeposit viewPrematureDetails(ChildrensDeposit childrensDeposit) {
        Date depositDate = childrensDeposit.getCreatedOn();
        Date todayDate = getTodayDate();
        double days = calculateDaysBetweenDate(depositDate, todayDate);
        double years = days / 365;
        BigDecimal periodOfDeposit = new BigDecimal(years);
        BigDecimal interest = calculateInterest(childrensDeposit.getDepositAmount(), periodOfDeposit, childrensDeposit.getRateOfInterest());
        if (interest.intValue() < 0) {
            interest = new BigDecimal(0);
        }
        BigDecimal maturityAmount = calculateMaturityAmount(childrensDeposit.getDepositAmount(), interest);
        childrensDeposit.setPreMatureInterestAmount(interest);
        childrensDeposit.setPreMatureAmount(maturityAmount);
        childrensDeposit.setPreMaturePeriodOfDeposit(periodOfDeposit);
        childrensDeposit.setPreMatureDate(todayDate);
        childrensDeposit.setPreMatureRateOfInterest(childrensDeposit.getRateOfInterest());
        LocalDate fromDate = depositDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = (Period) Period.between(fromDate, toDate);
        String duration = "Difference is " + diff.getYears() + " years," + diff.getMonths() + " months, and " + diff.getDays() + " days old";
        childrensDeposit.setDuration(duration);
        return childrensDeposit;
    }

    @PostMapping("/CHILDRENS_DEPOSIT/refund/pre-mature/fine-calculation")
    public ResponseEntity<ChildrensDeposit> refundPreMatureFineCalculation(@RequestBody DepositReportDto depositReportDto) throws ParseException {
        bodDateService.checkBOD();
        Optional<ChildrensDeposit> persistedDeposit = childrensDepositService.getChildrenDepositByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("ChildrensDeposit not found or not approved");
        }
        ChildrensDeposit childrensDeposit = persistedDeposit.get();
        ChildrensDeposit persisted = savePrematureDetails(childrensDeposit, depositReportDto);
        return new ResponseEntity<>(persisted, HttpStatus.OK);
    }

    private ChildrensDeposit savePrematureDetails(ChildrensDeposit childrensDeposit, DepositReportDto depositReportDto) {
        Date depositDate = childrensDeposit.getCreatedOn();
        Date todayDate = getTodayDate();
        double days = calculateDaysBetweenDate(depositDate, todayDate);
        LocalDate fromDate = depositDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = (Period) Period.between(fromDate, toDate);
        double years = days / 365;
        BigDecimal periodOfDeposit = new BigDecimal(years);
        BigDecimal interest = calculateInterest(childrensDeposit.getDepositAmount(), periodOfDeposit, childrensDeposit.getRateOfInterest());
        if (interest.intValue() < 0) {
            interest = new BigDecimal(0);
        }
        BigDecimal maturityAmount = calculateMaturityAmount(childrensDeposit.getDepositAmount(), interest);
        childrensDeposit.setPreMatureInterestAmount(interest);
        childrensDeposit.setPreMatureFine(depositReportDto.getPreMatureFine());
        childrensDeposit.setPreMatureAmount(maturityAmount.subtract(depositReportDto.preMatureFine));
        childrensDeposit.setPreMaturePeriodOfDeposit(periodOfDeposit);
        childrensDeposit.setPreMatureDate(todayDate);
        childrensDeposit.setPreMatureRateOfInterest(childrensDeposit.getRateOfInterest());
        childrensDeposit.setMaturityAmount(maturityAmount.subtract(depositReportDto.preMatureFine));
        String duration = diff.getYears() + " years," + diff.getMonths() + " months, and " + diff.getDays() + " days old";
        childrensDeposit.setDuration(duration);
        ChildrensDeposit persisted = childrensDepositService.saveDeposit(childrensDeposit);
        return persisted;
    }

    @GetMapping("test-api")
    public ResponseEntity<String> getApi() throws IOException {
        ReportDownload.writeXLSXFile();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}