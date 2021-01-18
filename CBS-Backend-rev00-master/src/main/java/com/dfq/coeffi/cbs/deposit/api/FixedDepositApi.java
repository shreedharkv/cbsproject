package com.dfq.coeffi.cbs.deposit.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLog;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.Dto.DepositApprovalDto;
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
import com.dfq.coeffi.cbs.utils.TransactionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.CREDIT;
import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.DEBIT;
import static com.dfq.coeffi.cbs.utils.DateUtil.calculateDaysBetweenDate;
import static com.dfq.coeffi.cbs.utils.DateUtil.getTodayDate;
import static com.dfq.coeffi.cbs.utils.DepositInterestCalculation.calculateInterest;
import static com.dfq.coeffi.cbs.utils.DepositInterestCalculation.calculateMaturityAmount;

@Slf4j
@RestController
public class FixedDepositApi extends BaseController {

    private final FixedDepositService fixedDepositService;
    private final DepositsApprovalService depositsApprovalService;
    private final MemberService memberService;
    private final DepositTransactionService depositTransactionService;
    private final ApplicationLogService applicationLogService;
    public final BankService bankService;
    public final SavingBankTransactionService savingBankTransactionService;
    public final AccountFormatService accountFormatService;
    long defaultApplicationNumber = 201900000;

    @Autowired
    private DepositAccountNumberMasterService depositAccountNumberMasterService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountHeadService accountHeadService;

    @Autowired
    private BODDateService bodDateService;

    @Autowired
    private SavingsBankDepositService savingsBankDepositService;

    @Autowired
    public FixedDepositApi(FixedDepositService fixedDepositService, DepositsApprovalService depositsApprovalService,
                           MemberService memberService, DepositTransactionService depositTransactionService, ApplicationLogService applicationLogService,
                           final BankService bankService, SavingBankTransactionService savingBankTransactionService,
                           final AccountFormatService accountFormatService) {
        this.fixedDepositService = fixedDepositService;
        this.depositsApprovalService = depositsApprovalService;
        this.memberService = memberService;
        this.applicationLogService = applicationLogService;
        this.depositTransactionService = depositTransactionService;
        this.bankService = bankService;
        this.savingBankTransactionService = savingBankTransactionService;
        this.accountFormatService = accountFormatService;
    }

    @PostMapping("/fixed-deposit/unapprove-list")
    public ResponseEntity<List<FixedDeposit>> getAllFixedDeposits(@RequestBody DepositApprovalDto depositApprovalDto) {
        List<FixedDeposit> fixedDepositList = fixedDepositService.getAllFixedDeposits(depositApprovalDto.dateFrom, depositApprovalDto.dateTo);
        if (CollectionUtils.isEmpty(fixedDepositList)) {
            throw new EntityNotFoundException("fixedDepositList");
        }
        return new ResponseEntity<>(fixedDepositList, HttpStatus.OK);
    }

    @PostMapping("/fixed-deposit")
    public ResponseEntity<FixedDeposit> createFixedDeposit(@Valid @RequestBody final FixedDeposit fixedDeposit, Principal principal) {
        bodDateService.checkBOD();
        Ledger ledger = accountHeadService.getLedgerByName("Fixed Deposit");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head and ledger not found");
        }
        AccountFormat accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.DEPOSIT, "FIXED_DEPOSIT");
        if (accountFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate account number");
        }
        String accountNumber = accountFormat.getPrefix() + "-" + (accountFormat.getFromAccountNumber() + 1);

        NumberFormat numberFormat = memberService.getNumberFormatByType("Deposit_Receipt_Number");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate receipt number");
        }
        String receiptNumber = numberFormat.getPrefix() + "-" + (numberFormat.getReceiptNumber() + 1);
        Optional<Member> memberObj = memberService.getMember(fixedDeposit.getMember().getId());
        if (!memberObj.isPresent()) {
            log.warn("Unable to find member with ID : {} not found", fixedDeposit.getMember().getId());
            throw new EntityNotFoundException(Member.class.getName());
        }
        Member member = memberObj.get();
        if (member.getMemberType() == MemberType.NOMINAL) {
            throw new EntityNotFoundException("Selected Member is nominal member, can not open Fixed Deposit");
        }
        fixedDeposit.setMember(member);
        savingsBankDepositService.checkSavingBankAccount(member); //check SB Account
        if(fixedDeposit.getPeriodOfName().equalsIgnoreCase("Yearly")){
            fixedDeposit.setMaturityDate(DateUtil.addYearsToDate(fixedDeposit.getPeriodOfDeposit().intValue()));
            BigDecimal interest = calculateInterest(fixedDeposit.getDepositAmount(), fixedDeposit.getPeriodOfDeposit(), fixedDeposit.getRateOfInterest());
            BigDecimal maturityAmount = calculateMaturityAmount(fixedDeposit.getDepositAmount(), interest);
            fixedDeposit.setInterestAmount(interest);
            fixedDeposit.setMaturityAmount(maturityAmount);
        } else if(fixedDeposit.getPeriodOfName().equalsIgnoreCase("Monthly")){
            fixedDeposit.setMaturityDate(DateUtil.addMonthsToDate(fixedDeposit.getPeriodOfDeposit().intValue()));
            BigDecimal convertToYear = BigDecimal.valueOf(fixedDeposit.getPeriodOfDeposit().doubleValue()/12);
            BigDecimal interest = calculateInterest(fixedDeposit.getDepositAmount(), convertToYear, fixedDeposit.getRateOfInterest());
            BigDecimal maturityAmount = calculateMaturityAmount(fixedDeposit.getDepositAmount(), interest);
            fixedDeposit.setInterestAmount(interest);
            fixedDeposit.setMaturityAmount(maturityAmount);
        }

        fixedDeposit.setApproved(false);
        fixedDeposit.setWithDrawn(false);
        fixedDeposit.setVoucherType("RECEIPT");
        fixedDeposit.setTransactionType("CREDIT");
        fixedDeposit.setTransactionBy(getLoggedUser(principal));
        fixedDeposit.setLastUpdatedDate(DateUtil.getTodayDate());
        Optional<DepositAccountNumberMaster> accountNumberMaster = depositAccountNumberMasterService.getDepositAccountNumberMasterById(1);
//        fixedDeposit.setAccountNumber(accountNumberMaster.get().getAccountNumberFD());
        fixedDeposit.setApplicationNumber(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberFD()) + defaultApplicationNumber));
        accountNumberMaster.get().setAccountNumberFD(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberFD()) + 1));
        depositAccountNumberMasterService.saveDepositAccountNumberMaster(accountNumberMaster.get());
        fixedDeposit.setAccountHead(ledger.getAccountHead());
        fixedDeposit.setLedger(ledger);
        fixedDeposit.setAccountNumber(accountNumber);
        fixedDeposit.setReceiptNumber(receiptNumber);

        FixedDeposit persistedDeposit = fixedDepositService.saveFixedDeposit(fixedDeposit);
        if (persistedDeposit != null) {
            User loggedUser = getLoggedUser(principal);
            ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "FIXED DEPOSIT ACCOUNT CREATED", "FIXED DEPOSIT ACCOUNT CREATED POST METHOD", loggedUser.getId());
            accountFormat.setFromAccountNumber(accountFormat.getFromAccountNumber() + 1);
            accountFormatService.saveAccountNumber(accountFormat);

            numberFormat.setReceiptNumber(numberFormat.getReceiptNumber() + 1);
            memberService.updateNumberFormat(numberFormat);
        }
        return new ResponseEntity<>(persistedDeposit, HttpStatus.CREATED);
    }

    @PutMapping("/fixed-deposit/{id}")
    public ResponseEntity<FixedDeposit> updateFixedDeposit(@PathVariable long id, @Valid @RequestBody FixedDeposit fixedDeposit, Principal principal) {
        Optional<FixedDeposit> persistedFixedDeposit = fixedDepositService.getFixedDepositById(id);
        if (!persistedFixedDeposit.isPresent()) {
            throw new EntityNotFoundException(FixedDeposit.class.getSimpleName());
        }
        fixedDeposit.setId(id);
        fixedDeposit.setCreatedOn(persistedFixedDeposit.get().getCreatedOn());
        FixedDeposit saveFixedDeposit = fixedDepositService.saveFixedDeposit(fixedDeposit);
        if (saveFixedDeposit != null) {
            User loggedUser = getLoggedUser(principal);
            ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "FIXED DEPOSIT ACCOUNT Modified", "FIXED DEPOSIT ACCOUNT Modified PUT METHOD", loggedUser.getId());
        }
        return new ResponseEntity<>(fixedDeposit, HttpStatus.OK);
    }

    @PostMapping("/FIXED_DEPOSIT/add-nominee/{id}")
    public ResponseEntity<FixedDeposit> addNomineeDetails(@PathVariable long id, @Valid @RequestBody DepositNomineeDto depositNomineeDto) {
        Optional<FixedDeposit> persistedDeposit = fixedDepositService.getFixedDepositById(id);
        FixedDeposit fixedDeposit = persistedDeposit.get();
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException(FixedDeposit.class.getSimpleName());
        }
        if (fixedDeposit.getDepositNomineeDetailsTwo() == null) {
            fixedDeposit.setDepositNomineeDetailsTwo(depositNomineeDto.getDepositNomineeDetailsTwo());
        } else if (fixedDeposit.getDepositNomineeDetailsThree() == null) {
            fixedDeposit.setDepositNomineeDetailsThree(depositNomineeDto.getDepositNomineeDetailsThree());
        }
        FixedDeposit saveFixedDeposit = fixedDepositService.saveFixedDeposit(fixedDeposit);
        return new ResponseEntity<>(saveFixedDeposit, HttpStatus.OK);
    }

    @DeleteMapping("/fixed-deposit/{id}")
    public ResponseEntity<FixedDeposit> deleteFixedDeposit(@PathVariable Long id) {
        Optional<FixedDeposit> fixedDeposit = fixedDepositService.getFixedDepositById(id);
        if (!fixedDeposit.isPresent()) {
            throw new EntityNotFoundException(FixedDeposit.class.getName());
        }
        fixedDepositService.deleteFixedDeposit(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("fixed-deposit/account-number")
    public ResponseEntity<FixedDeposit> getFixedDepositByAccountNumber(@RequestBody DepositReportDto depositReportDto) {
        Optional<FixedDeposit> persistedDeposit = fixedDepositService.getFixedDepositByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("No Active Deposits found for the account number: " + depositReportDto.accountNumber);
        }
        return new ResponseEntity(persistedDeposit.get(), HttpStatus.OK);
    }

    @PostMapping("/fixed-deposit/approval")
    public ResponseEntity<FixedDeposit> fixedDepositApproval(@RequestBody DepositApprovalDto depositApprovalDto, Principal principal) {
        DepositsApproval depositsApproval = saveDepositsApproval();

        Optional<FixedDeposit> persistedFixedDeposit = null;
        for (long id : depositApprovalDto.ids) {
            persistedFixedDeposit = fixedDepositService.getFixedDepositById(id);
            if (!persistedFixedDeposit.isPresent()) {
                throw new EntityNotFoundException(FixedDeposit.class.getSimpleName());
            }
            persistedFixedDeposit.get().setDepositsApproval(depositsApproval);
            persistedFixedDeposit.get().setStatus(true);
            persistedFixedDeposit.get().setWithDrawn(false);
            FixedDeposit fixedDeposit = fixedDepositService.saveFixedDeposit(persistedFixedDeposit.get());
            DepositTransaction depositTransaction = saveDepositTransaction(persistedFixedDeposit.get());
            if (fixedDeposit != null) {
                transactionCreditEntry(fixedDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(fixedDeposit.getDepositAmount(), "CREDIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "FIXED DEPOSIT ACCOUNT Modified", "FIXED DEPOSIT ACCOUNT Modified PUT METHOD", loggedUser.getId());
            }
        }
        return new ResponseEntity<>(persistedFixedDeposit.get(), HttpStatus.OK);
    }

    public DepositsApproval saveDepositsApproval() {
        DepositsApproval depositsApproval = new DepositsApproval();
        depositsApproval.setApproved(true);
        depositsApproval.setRemarks(depositsApproval.getRemarks());
        depositsApprovalService.saveDepositsApproval(depositsApproval);
        return depositsApproval;
    }

    public DepositTransaction saveDepositTransaction(FixedDeposit fixedDeposit) {
        DepositTransaction depositTransaction = new DepositTransaction();
        depositTransaction.setAccountNumber(fixedDeposit.getAccountNumber());
        depositTransaction.setDepositType(fixedDeposit.getDepositType());
        depositTransaction.setDepositAmount(fixedDeposit.getDepositAmount());
        depositTransaction.setInterestAmount(fixedDeposit.getInterestAmount());
        depositTransaction.setMember(fixedDeposit.getMember());
        depositTransaction.setTransactionType(TransactionType.CREDIT);
        depositTransaction.setBalanceAmount(fixedDeposit.getMaturityAmount());
        depositTransactionService.createDepositTransactionService(depositTransaction);
        return depositTransaction;
    }

    public DepositTransaction withDraw(FixedDeposit fixedDeposit) {
        DepositTransaction depositTransaction = new DepositTransaction();
        depositTransaction.setAccountNumber(fixedDeposit.getAccountNumber());
        depositTransaction.setDepositType(fixedDeposit.getDepositType());
        depositTransaction.setMember(fixedDeposit.getMember());
        depositTransaction.setTransactionType(TransactionType.DEBIT);
        depositTransaction.setWithDrawAmount(fixedDeposit.getMaturityAmount());
        depositTransactionService.createDepositTransactionService(depositTransaction);
        return depositTransaction;
    }

    @PostMapping("/FIXED_DEPOSIT/refund")
    public ResponseEntity<FixedDeposit> refundFixedDeposit(@RequestBody DepositReportDto depositReportDto, Principal principal) {
        bodDateService.checkBOD();
        Optional<FixedDeposit> fixedDeposit = fixedDepositService.getFixedDepositByAccountNumber(depositReportDto.accountNumber);
        if (!fixedDeposit.isPresent()) {
            throw new EntityNotFoundException("FixedDeposit not found or not approved");
        }
        fixedDepositService.checkFixedDepositForLoan(fixedDeposit.get().getAccountNumber());

        fixedDeposit.get().setId(fixedDeposit.get().getId());
        fixedDeposit.get().setWithDrawn(true);
        fixedDeposit.get().setAccountClosedOn(DateUtil.getTodayDate());

        fixedDeposit.get().setModeOfPayment(depositReportDto.getModeOfPayment());
        FixedDeposit persistedDeposit = fixedDepositService.saveFixedDeposit(fixedDeposit.get());
        if (depositReportDto.getModeOfPayment().equalsIgnoreCase("Cash")) {
            if (persistedDeposit != null) {
                transactionDebitEntry(persistedDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(persistedDeposit.getMaturityAmount(), "DEBIT");
            }
        } else if (depositReportDto.getModeOfPayment().equalsIgnoreCase("Bank")) {
            if (persistedDeposit != null) {
                transactionDebitEntry(persistedDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(persistedDeposit.getMaturityAmount(), "DEBIT");
            }
        } else if (depositReportDto.getModeOfPayment().equalsIgnoreCase("Transfer")) {
            if (persistedDeposit != null) {
                User loggedUser = getLoggedUser(principal);
                transactionDebitEntry(persistedDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(persistedDeposit.getMaturityAmount(), "DEBIT");
                depositRefundCreditTransactionEntry(persistedDeposit, loggedUser);
            }
        }
        if (persistedDeposit != null) {
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Fixed deposit refunded Account number" + persistedDeposit.getAccountNumber(), "CHILDRENS DEPOSIT POSTED", loggedUser.getId());
        }
        DepositTransaction depositTransaction = withDraw(fixedDeposit.get());
        return new ResponseEntity<>(fixedDeposit.get(), HttpStatus.OK);
    }

    @PostMapping("/FIXED_DEPOSIT/by-customer-id")
    public ResponseEntity<List<FixedDeposit>> fixedDepositByCustomerId(@RequestBody DepositReportDto depositReportDto) {
        List<FixedDeposit> fixedDeposit = fixedDepositService.getFixedDepositByCustomerId(depositReportDto.customerId);
        if (fixedDeposit.isEmpty()) {
            throw new EntityNotFoundException(FixedDeposit.class.getSimpleName());
        }
        return new ResponseEntity<>(fixedDeposit, HttpStatus.OK);
    }

    private void transactionCreditEntry(FixedDeposit fixedDeposit) {

        Ledger ledger = accountHeadService.getLedgerByName("Fixed Deposit");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Initial Amount Credited for fixedDeposit Account " + fixedDeposit.getId());
            creditTransaction.setCreditAmount(fixedDeposit.getDepositAmount());
            creditTransaction.setTransactionBy(fixedDeposit.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setParticulars("FIXED DEPOSIT");
            creditTransaction.setTransferType(fixedDeposit.getModeOfPayment());
            creditTransaction.setTransactionType(fixedDeposit.getTransactionType());
            creditTransaction.setVoucherType(fixedDeposit.getVoucherType());
            creditTransaction.setOtherFees(BigDecimal.ZERO);
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName, CREDIT);
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountNumber(fixedDeposit.getAccountNumber());
            creditTransaction.setAccountName(fixedDeposit.getMember().getName() + " (" + fixedDeposit.getMember().getMemberNumber() + ")");

            Transaction persistedDebitTransaction = transactionService.transactionEntry(creditTransaction);
        }
    }

    private void transactionDebitEntry(FixedDeposit fixedDeposit) {
        Ledger ledger = accountHeadService.getLedgerByName("Fixed Deposit");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(fixedDeposit.getMaturityAmount());
            debitTransaction.setRemark("Amount debited for FIXED DEPOSIT Withdrawn " + fixedDeposit.getId());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(fixedDeposit.getTransactionBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setParticulars("FIXED DEPOSIT");
            debitTransaction.setOtherFees(BigDecimal.ZERO);

            debitTransaction.setVoucherType(fixedDeposit.getVoucherType());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName, DEBIT);
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountNumber(fixedDeposit.getAccountNumber());
            debitTransaction.setTransactionBy(fixedDeposit.getTransactionBy());
            debitTransaction.setTransferType(fixedDeposit.getModeOfPayment());
            debitTransaction.setAccountName(fixedDeposit.getMember().getName() + " (" + fixedDeposit.getMember().getMemberNumber() + ")");
            transactionService.transactionEntry(debitTransaction);
        }
    }

    private void depositRefundCreditTransactionEntry(FixedDeposit fixedDeposit, User user) {

        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(fixedDeposit.getMember().getMemberNumber());

        if (savingsBankDeposit == null) {
            throw new EntityNotFoundException("Saving Bank Account Not Found ");
        }

        Ledger ledger = null;
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
            depositBalance = depositBalance.add(fixedDeposit.getMaturityAmount());

            SavingBankTransaction sbTransaction = new SavingBankTransaction();

            sbTransaction.setTransactionType("CREDIT");
            sbTransaction.setCreditAmount(fixedDeposit.getMaturityAmount());

            sbTransaction.setBalance(depositBalance);
            sbTransaction.setDebitAmount(BigDecimal.ZERO);
            sbTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
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
            transactionUtil.getUpdateSocietyBalance(fixedDeposit.getMaturityAmount(), "CREDIT");
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For debit transaction against the refund share
            Transaction creditTransaction = new Transaction();

            creditTransaction.setRemark("Amount credited to Saving Bank Acc No. : " + savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setCreditAmount(fixedDeposit.getMaturityAmount());
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setOtherFees(BigDecimal.ZERO);
            creditTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransferType(fixedDeposit.getModeOfPayment());
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountName(fixedDeposit.getMember().getName() + " (" + fixedDeposit.getMember().getMemberNumber() + ")");

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(fixedDeposit.getMaturityAmount());
            creditTransaction.setBalance(balance);

            transactionService.transactionEntry(creditTransaction);

            if (creditTransaction != null) {
                String message = "" + creditTransaction.getCreditAmount() + " Amount credited to saving bank Acc No. : " + savingsBankDeposit.getAccountNumber() + "From : " + fixedDeposit.getMember().getName();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "FixedDeposit Deposit Module", user.getId());
            }
            if (creditTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credit from society share bank Acc No. " + creditTransaction.getId() + " submitted",
                        "FIXED DEPOSIT REFUNDED", user.getId());
            }
        }
    }

    @PostMapping("/FIXED_DEPOSIT/refund/pre-mature")
    public ResponseEntity<FixedDeposit> refundPreMatureFixedDeposit(@RequestBody DepositReportDto depositReportDto) throws ParseException {
        bodDateService.checkBOD();
        Optional<FixedDeposit> persistedDeposit = fixedDepositService.getFixedDepositByAccountNumber(depositReportDto.accountNumber);
        fixedDepositService.checkFixedDepositForLoan(persistedDeposit.get().getAccountNumber());
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("FixedDeposit not found or not approved");
        }
        FixedDeposit fixedDeposit = persistedDeposit.get();
        FixedDeposit persisted = viewPrematureDetails(fixedDeposit, depositReportDto);
        /**
         * Transaction Entry
         */
        transactionPrematureFine(persisted);
        return new ResponseEntity<>(persisted, HttpStatus.OK);
    }

    private FixedDeposit viewPrematureDetails(FixedDeposit fixedDeposit, DepositReportDto depositReportDto) {
        Date depositDate = fixedDeposit.getCreatedOn();
        Date todayDate = getTodayDate();
        double days = calculateDaysBetweenDate(depositDate, todayDate);
        double years = days / 365;
        BigDecimal periodOfDeposit = new BigDecimal(years);
        BigDecimal interest = calculateInterest(fixedDeposit.getDepositAmount(), periodOfDeposit, fixedDeposit.getRateOfInterest());
        BigDecimal maturityAmount = calculateMaturityAmount(fixedDeposit.getDepositAmount(), interest);
        fixedDeposit.setPreMatureInterestAmount(interest);
        fixedDeposit.setPreMatureFine(depositReportDto.getPreMatureFine());

        fixedDeposit.setPreMatureAmount(maturityAmount);
        fixedDeposit.setPreMaturePeriodOfDeposit(periodOfDeposit);
        fixedDeposit.setPreMatureDate(todayDate);
        fixedDeposit.setPreMatureRateOfInterest(fixedDeposit.getRateOfInterest());
        LocalDate fromDate = depositDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = (Period) Period.between(fromDate, toDate);
        String duration = "Difference is " + diff.getYears() + " years," + diff.getMonths() + " months, and " + diff.getDays() + " days old";
        fixedDeposit.setDuration(duration);
        return fixedDeposit;
    }

    @PostMapping("/FIXED_DEPOSIT/refund/pre-mature/fine-calculation")
    public ResponseEntity<FixedDeposit> refundPreMatureFineCalculation(@RequestBody DepositReportDto depositReportDto) throws ParseException {
        bodDateService.checkBOD();
        Optional<FixedDeposit> persistedDeposit = fixedDepositService.getFixedDepositByAccountNumber(depositReportDto.accountNumber);
        fixedDepositService.checkFixedDepositForLoan(persistedDeposit.get().getAccountNumber());
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("FixedDeposit not found or not approved");
        }
        FixedDeposit fixedDeposit = persistedDeposit.get();
        FixedDeposit persisted = savePrematureDetails(fixedDeposit, depositReportDto);
        return new ResponseEntity<>(persisted, HttpStatus.OK);
    }

    private FixedDeposit savePrematureDetails(FixedDeposit fixedDeposit, DepositReportDto depositReportDto) {
        Date depositDate = fixedDeposit.getCreatedOn();
        Date todayDate = getTodayDate();
        double days = calculateDaysBetweenDate(depositDate, todayDate);
        double years = days / 365;
        BigDecimal periodOfDeposit = new BigDecimal(years);
        BigDecimal interest = calculateInterest(fixedDeposit.getDepositAmount(), periodOfDeposit, fixedDeposit.getRateOfInterest());
        BigDecimal maturityAmount = calculateMaturityAmount(fixedDeposit.getDepositAmount(), interest);
        fixedDeposit.setPreMatureInterestAmount(interest);
        fixedDeposit.setPreMatureFine(depositReportDto.getPreMatureFine());
        fixedDeposit.setPreMatureAmount(maturityAmount.subtract(depositReportDto.preMatureFine));
        fixedDeposit.setPreMaturePeriodOfDeposit(periodOfDeposit);
        fixedDeposit.setPreMatureDate(todayDate);
        fixedDeposit.setPreMatureRateOfInterest(fixedDeposit.getRateOfInterest());
        LocalDate fromDate = depositDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = (Period) Period.between(fromDate, toDate);
        String duration = diff.getYears() + " years," + diff.getMonths() + " months, and " + diff.getDays() + " days old";
        fixedDeposit.setDuration(duration);
        fixedDeposit.setMaturityAmount(maturityAmount.subtract(depositReportDto.preMatureFine));
        FixedDeposit persisted = fixedDepositService.saveFixedDeposit(fixedDeposit);

        return persisted;
    }

    private void transactionPrematureFine(FixedDeposit fixedDeposit) {

        Ledger ledger = accountHeadService.getLedgerByName("Penalty Interest");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found for Penalty Interest");
        }
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setCreditAmount(fixedDeposit.getPreMatureFine());
            creditTransaction.setRemark("PREMATURE FINE AMOUNT" + fixedDeposit.getId());
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setTransactionBy(fixedDeposit.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setParticulars("PREMATURE FINE AMOUNT");
            creditTransaction.setOtherFees(BigDecimal.ZERO);

            creditTransaction.setVoucherType(fixedDeposit.getVoucherType());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName, CREDIT);
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountNumber(fixedDeposit.getAccountNumber());
            creditTransaction.setTransactionBy(fixedDeposit.getTransactionBy());
            creditTransaction.setTransferType("PROFIT");
            creditTransaction.setAccountName(fixedDeposit.getMember().getName() + " (" + fixedDeposit.getMember().getMemberNumber() + ")");
            transactionService.transactionEntry(creditTransaction);
        }
    }
}
