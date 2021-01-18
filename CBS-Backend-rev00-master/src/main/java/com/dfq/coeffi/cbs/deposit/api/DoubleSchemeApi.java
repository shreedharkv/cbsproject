package com.dfq.coeffi.cbs.deposit.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLog;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.Dto.DepositApprovalDto;
import com.dfq.coeffi.cbs.deposit.Dto.DepositNomineeDto;
import com.dfq.coeffi.cbs.deposit.constants.DepositApiConstants;
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
public class DoubleSchemeApi extends BaseController {
    private final DoubleSchemeService doubleSchemeService;
    private final ChildrensDepositApi childrensDepositApi;
    private final DepositsApprovalService depositsApprovalService;
    private final MemberService memberService;
    private final ApplicationLogService applicationLogService;
    private final DepositTransactionService depositTransactionService;
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
    private DoubleSchemeApi(DoubleSchemeService doubleSchemeService, ChildrensDepositApi childrensDepositApi, DepositsApprovalService depositsApprovalService,
                            MemberService memberService, ApplicationLogService applicationLogService, DepositTransactionService depositTransactionService,
                            final BankService bankService, SavingBankTransactionService savingBankTransactionService,
                            final AccountFormatService accountFormatService) {
        this.doubleSchemeService = doubleSchemeService;
        this.childrensDepositApi = childrensDepositApi;
        this.depositsApprovalService = depositsApprovalService;
        this.memberService = memberService;
        this.applicationLogService = applicationLogService;
        this.depositTransactionService = depositTransactionService;
        this.bankService = bankService;
        this.savingBankTransactionService = savingBankTransactionService;
        this.accountFormatService = accountFormatService;
    }

   @PostMapping("/double-scheme/unapprove-list")
    public ResponseEntity<List<DoubleScheme>> getAllDoubleSchemeDeposists(@RequestBody DepositApprovalDto depositApprovalDto) {
        List<DoubleScheme> doubleSchemeList = doubleSchemeService.getAllDoubleSchemeDeposits(depositApprovalDto.dateFrom,depositApprovalDto.dateTo);
        if (CollectionUtils.isEmpty(doubleSchemeList)) {
            throw new EntityNotFoundException("doubleSchemeList");
        }
        return new ResponseEntity<>(doubleSchemeList,HttpStatus.OK);
    }

    @PostMapping("/double-scheme")
    public ResponseEntity<DoubleScheme> createDoubleScheme(@Valid @RequestBody final DoubleScheme doubleScheme,Principal principal) {
        bodDateService.checkBOD();
        Ledger ledger = accountHeadService.getLedgerByName("Double Scheme");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head and ledger not found");
        }

        AccountFormat accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.DEPOSIT, "DOUBLE_SCHEME");
        if (accountFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate account number");
        }
        String accountNumber = accountFormat.getPrefix() + "-" + (accountFormat.getFromAccountNumber() + 1);

        NumberFormat numberFormat = memberService.getNumberFormatByType("Deposit_Receipt_Number");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate receipt number");
        }
        String receiptNumber = numberFormat.getPrefix() + "-" + (numberFormat.getReceiptNumber() + 1);

        Optional<Member> memberObj = memberService.getMember(doubleScheme.getMember().getId());
        if (!memberObj.isPresent()) {
            log.warn("Unable to find member with ID : {} not found",doubleScheme.getMember().getId());
            throw new EntityNotFoundException(Member.class.getName());
        }
        Member member = memberObj.get();
        if(member.getMemberType() == MemberType.NOMINAL){
            throw new EntityNotFoundException("Selected Member is nominal member, can not open Double Scheme Deposit");
        }
        doubleScheme.setMember(member);

        savingsBankDepositService.checkSavingBankAccount(member);
        BigDecimal periodInYears = DepositApiConstants.DOUBLE_SCHEME_DEPOSIT_PERIOD.divide(DepositApiConstants.MONTHS_TO_YEAR);
        BigDecimal interest = childrensDepositApi.calculateInterest(doubleScheme.getDepositAmount(),periodInYears,DepositApiConstants.DOUBLE_SCHEME_RATE_OF_INTEREST);
        BigDecimal maturityAmount = childrensDepositApi.calculateMaturityAmount(doubleScheme.getDepositAmount(),interest);
        BigDecimal d = DepositApiConstants.DOUBLE_SCHEME_DEPOSIT_PERIOD;
        doubleScheme.setMaturityDate(DateUtil.addMonthsToDate(d.intValue()));
        doubleScheme.setInterestAmount(interest);
        doubleScheme.setMaturityAmount(maturityAmount);
        doubleScheme.setVoucherType("RECEIPT");
        doubleScheme.setTransactionType("CREDIT");
        doubleScheme.setTransactionBy(getLoggedUser(principal));
        doubleScheme.setRateOfInterest(DepositApiConstants.DOUBLE_SCHEME_RATE_OF_INTEREST);
        doubleScheme.setPeriodOfDeposit(DepositApiConstants.DOUBLE_SCHEME_DEPOSIT_PERIOD);
        Optional<DepositAccountNumberMaster> accountNumberMaster = depositAccountNumberMasterService.getDepositAccountNumberMasterById(1);
//        doubleScheme.setAccountNumber(accountNumberMaster.get().getAccountNumberADS());
        doubleScheme.setApplicationNumber(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberADS())+defaultApplicationNumber));
        accountNumberMaster.get().setAccountNumberADS(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberADS()) + 1));
        depositAccountNumberMasterService.saveDepositAccountNumberMaster(accountNumberMaster.get());
        doubleScheme.setAccountHead(ledger.getAccountHead());
        doubleScheme.setLedger(ledger);
        doubleScheme.setAccountNumber(accountNumber);
        doubleScheme.setReceiptNumber(receiptNumber);

        DoubleScheme persistedDeposit = doubleSchemeService.saveDoubleSchemeDeposit(doubleScheme);
        if(persistedDeposit != null){
            User loggedUser = getLoggedUser(principal);
            ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"DOUBLE SCHEME ACCOUNT CREATED","DOUBLE SCHEME POST METHOD",loggedUser.getId());
            accountFormat.setFromAccountNumber(accountFormat.getFromAccountNumber() + 1);
            accountFormatService.saveAccountNumber(accountFormat);

            numberFormat.setReceiptNumber(numberFormat.getReceiptNumber() + 1);
            memberService.updateNumberFormat(numberFormat);
        }
        return new ResponseEntity<>(persistedDeposit,HttpStatus.CREATED);
    }

    @PutMapping("/double-scheme/{id}")
    public ResponseEntity<DoubleScheme> updateDoubleScheme(@PathVariable long id,@Valid @RequestBody DoubleScheme doubleScheme,Principal principal) {
        Optional<DoubleScheme> persistedDoubleScheme = doubleSchemeService.getDoubleSchemeDepositById(id);
        if (!persistedDoubleScheme.isPresent()) {
            throw new EntityNotFoundException(DoubleScheme.class.getSimpleName());
        }
        doubleScheme.setId(id);
        doubleScheme.setCreatedOn(persistedDoubleScheme.get().getCreatedOn());
        DoubleScheme saveDoubleScheme = doubleSchemeService.saveDoubleSchemeDeposit(doubleScheme);
        if(saveDoubleScheme != null){
            transactionCreditEntry(doubleScheme);
            User loggedUser = getLoggedUser(principal);
            ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"DOUBLE SCHEME ACCOUNT Modified","DOUBLE SCHEME PUT METHOD",loggedUser.getId());
        }
        return new ResponseEntity<>(doubleScheme,HttpStatus.OK);
    }

    @PostMapping("/DOUBLE_SCHEME/add-nominee/{id}")
    public ResponseEntity<DoubleScheme> addNomineeDetails(@PathVariable long id, @Valid @RequestBody DepositNomineeDto depositNomineeDto) {
        Optional<DoubleScheme> persistedDeposit = doubleSchemeService.getDoubleSchemeDepositById(id);
        DoubleScheme doubleScheme = persistedDeposit.get();
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException(DoubleScheme.class.getSimpleName());
        }
        if(doubleScheme.getDepositNomineeDetailsTwo() == null){
            doubleScheme.setDepositNomineeDetailsTwo(depositNomineeDto.getDepositNomineeDetailsTwo());
        }else if(doubleScheme.getDepositNomineeDetailsThree() == null){
            doubleScheme.setDepositNomineeDetailsThree(depositNomineeDto.getDepositNomineeDetailsThree());
        }
        DoubleScheme doubleSchemeDeposit = doubleSchemeService.saveDoubleSchemeDeposit(doubleScheme);
        return new ResponseEntity<>(doubleSchemeDeposit,HttpStatus.OK);
    }
    @DeleteMapping("/double-scheme/{id}")
    public ResponseEntity<DoubleScheme> deleteDoubleScheme(@PathVariable Long id) {
        Optional<DoubleScheme> doubleScheme = doubleSchemeService.getDoubleSchemeDepositById(id);
        if (!doubleScheme.isPresent()) {
            throw new EntityNotFoundException(DoubleScheme.class.getName());
        }
        doubleSchemeService.deleteDoubleSchemeDeposit(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/double-scheme/approval")
    public ResponseEntity<DoubleScheme> currentAccountApproval(@RequestBody DepositApprovalDto depositApprovalDto,Principal principal) {
        DepositsApproval depositsApproval = saveDepositsApproval(principal);
        Optional<DoubleScheme> persistedDoubleScheme = null;
        for (long id : depositApprovalDto.ids) {
            persistedDoubleScheme = doubleSchemeService.getDoubleSchemeDepositById(id);
            if (!persistedDoubleScheme.isPresent()) {
                throw new EntityNotFoundException(DoubleScheme.class.getSimpleName());
            }
            persistedDoubleScheme.get().setDepositsApproval(depositsApproval);
            persistedDoubleScheme.get().setStatus(true);
            DoubleScheme doubleScheme = doubleSchemeService.saveDoubleSchemeDeposit(persistedDoubleScheme.get());
            DepositTransaction depositTransaction = saveDepositTransaction(persistedDoubleScheme.get());
            if(doubleScheme != null){
                transactionCreditEntry(doubleScheme);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(doubleScheme.getDepositAmount(),"CREDIT");
                User loggedUser = getLoggedUser(principal);
                ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"DOUBLE SCHEME ACCOUNT APPROVED","DOUBLE SCHEME APPROVAL POST METHOD",loggedUser.getId());
            }
        }
        return new ResponseEntity<>(persistedDoubleScheme.get(),HttpStatus.OK);
    }

    public DepositsApproval saveDepositsApproval(Principal principal) {
        DepositsApproval depositsApproval = new DepositsApproval();
        depositsApproval.setApproved(true);
        depositsApproval.setRemarks(depositsApproval.getRemarks());
        depositsApproval.setApprovedBy(getLoggedUser(principal).getFirstName());
        depositsApprovalService.saveDepositsApproval(depositsApproval);
        return depositsApproval;
    }

    @PostMapping("double-scheme/account-number")
    public ResponseEntity<DoubleScheme> getDoubleSchemeByAccountNumber(@RequestBody DepositReportDto depositReportDto) {
        Optional<DoubleScheme> persistedDeposit = doubleSchemeService.getDoubleSchemeByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("No Active Deposits found for the account number: "+depositReportDto.accountNumber);
        }
        return new ResponseEntity(persistedDeposit.get(),HttpStatus.OK);
    }

    @PostMapping("/DOUBLE_SCHEME/refund")
    public ResponseEntity<DoubleScheme> refundDoubleScheme(@RequestBody DepositReportDto depositReportDto,Principal principal) {
        bodDateService.checkBOD();
        Optional<DoubleScheme> doubleScheme =doubleSchemeService.getDoubleSchemeByAccountNumber(depositReportDto.accountNumber);
        if (!doubleScheme.isPresent()) {
            throw new EntityNotFoundException("DoubleScheme not found or not approved");
        }
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null && (doubleScheme.get().getMaturityAmount().compareTo(latestTransaction.getBalance()) > 0))
            doubleScheme.get().setId(doubleScheme.get().getId());
        doubleScheme.get().setWithDrawn(true);
        doubleScheme.get().setAccountClosedOn(DateUtil.getTodayDate());

        doubleScheme.get().setModeOfPayment(depositReportDto.getModeOfPayment());
        DoubleScheme persistedDeposit = doubleSchemeService.saveDoubleSchemeDeposit(doubleScheme.get());
        if(depositReportDto.getModeOfPayment().equalsIgnoreCase("Cash")){
            if(persistedDeposit!=null) {
                transactionDebitEntry(persistedDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(persistedDeposit.getMaturityAmount(),"DEBIT");
            }
        }
        else if(depositReportDto.getModeOfPayment().equalsIgnoreCase("Bank")){
            if(persistedDeposit!=null) {
                transactionDebitEntry(persistedDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(persistedDeposit.getMaturityAmount(),"DEBIT");
            }
        }
        else if(depositReportDto.getModeOfPayment().equalsIgnoreCase("Transfer")){
            if(persistedDeposit!=null) {
                User loggedUser = getLoggedUser(principal);
                transactionDebitEntry(persistedDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(persistedDeposit.getMaturityAmount(),"DEBIT");
                depositRefundCreditTransactionEntry(persistedDeposit,loggedUser);
            }
        }
        if(persistedDeposit != null){
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"Double Scheme refunded Account number"+persistedDeposit.getAccountNumber(),"CHILDRENS DEPOSIT POSTED",loggedUser.getId());
        }
        DepositTransaction depositTransaction = withDraw(doubleScheme.get());
        return new ResponseEntity<>(doubleScheme.get(),HttpStatus.OK);
    }

    public DepositTransaction saveDepositTransaction(DoubleScheme doubleScheme){
        DepositTransaction depositTransaction=new DepositTransaction();
        depositTransaction.setAccountNumber(doubleScheme.getAccountNumber());
        depositTransaction.setDepositType(doubleScheme.getDepositType());
        depositTransaction.setDepositAmount(doubleScheme.getDepositAmount());
        depositTransaction.setInterestAmount(doubleScheme.getInterestAmount());
        depositTransaction.setMember(doubleScheme.getMember());
        depositTransaction.setTransactionType(TransactionType.CREDIT);
        depositTransaction.setBalanceAmount(doubleScheme.getMaturityAmount());
        depositTransactionService.createDepositTransactionService(depositTransaction);
        return depositTransaction;
    }

    public DepositTransaction withDraw(DoubleScheme doubleScheme){
        DepositTransaction depositTransaction=new DepositTransaction();
        depositTransaction.setAccountNumber(doubleScheme.getAccountNumber());
        depositTransaction.setDepositType(doubleScheme.getDepositType());
        depositTransaction.setMember(doubleScheme.getMember());
        depositTransaction.setTransactionType(TransactionType.DEBIT);
        depositTransaction.setWithDrawAmount(doubleScheme.getMaturityAmount());
        depositTransactionService.createDepositTransactionService(depositTransaction);
        return depositTransaction;
    }

    private void transactionCreditEntry(DoubleScheme doubleScheme) {
        Ledger ledger = accountHeadService.getLedgerByName("Double Scheme");
        if(ledger == null){
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Initial Amount Credited for doubleScheme Account " + doubleScheme.getId());
            creditTransaction.setCreditAmount(doubleScheme.getDepositAmount());
            creditTransaction.setTransactionBy(doubleScheme.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setTransferType("Cash");
            creditTransaction.setVoucherType(doubleScheme.getVoucherType());
            creditTransaction.setParticulars("DOUBLE SCHEME");
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName,CREDIT);
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountNumber(doubleScheme.getAccountNumber());
            creditTransaction.setAccountName(doubleScheme.getMember().getName() + " (" + doubleScheme.getMember().getMemberNumber() + ")");

            Transaction persistedDebitTransaction = transactionService.transactionEntry(creditTransaction);
        }
    }

    private void transactionDebitEntry(DoubleScheme doubleScheme) {
        Ledger ledger = accountHeadService.getLedgerByName("Double Scheme");
        if(ledger == null){
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null)  {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(doubleScheme.getMaturityAmount());
            debitTransaction.setRemark("Amount debited for DOUBLE SCHEME Withdrawn " + doubleScheme.getId());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(doubleScheme.getTransactionBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setParticulars("DOUBLE SCHEME");
            debitTransaction.setVoucherType(doubleScheme.getVoucherType());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName,DEBIT);
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountNumber(doubleScheme.getAccountNumber());
            debitTransaction.setTransactionBy(doubleScheme.getTransactionBy());
            debitTransaction.setTransferType(doubleScheme.getModeOfPayment());
            debitTransaction.setAccountName(doubleScheme.getMember().getName() + " (" + doubleScheme.getMember().getMemberNumber() + ")");

            Transaction persistedDebitTransaction = transactionService.transactionEntry(debitTransaction);
        }
    }

    private void depositRefundCreditTransactionEntry(DoubleScheme doubleScheme, User user){

        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(doubleScheme.getMember().getMemberNumber());

        if(savingsBankDeposit == null){
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
            depositBalance = depositBalance.add(doubleScheme.getMaturityAmount());

            SavingBankTransaction sbTransaction = new SavingBankTransaction();

            sbTransaction.setTransactionType("CREDIT");
            sbTransaction.setCreditAmount(doubleScheme.getMaturityAmount());

            sbTransaction.setBalance(depositBalance);
            sbTransaction.setDebitAmount(BigDecimal.ZERO);
            sbTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
            sbTransaction.setTransactionBy(user);
            sbTransaction.setSavingsBankDeposit(savingsBankDeposit);

            savingBankTransactionService.createSavingBankTransaction(sbTransaction);

            savingsBankDeposit.setBalance(depositBalance);

            savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

            if(savingBankTransaction != null){
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credited to saving bank Acc No." + savingBankTransaction.getAccountNumber() + " submitted",
                        "SAVING BANK TRANSACTION", user.getId());
            }
            TransactionUtil transactionUtil = new TransactionUtil(bankService);
            transactionUtil.getUpdateSocietyBalance(doubleScheme.getMaturityAmount(),"CREDIT");
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For debit transaction against the refund share
            Transaction creditTransaction = new Transaction();

            creditTransaction.setRemark("Amount credited to Saving Bank Acc No. : " + savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setCreditAmount(doubleScheme.getMaturityAmount());
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransferType(doubleScheme.getModeOfPayment());
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountName(doubleScheme.getMember().getName() + " (" + doubleScheme.getMember().getMemberNumber() + ")");


            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(doubleScheme.getMaturityAmount());
            creditTransaction.setBalance(balance);

            transactionService.transactionEntry(creditTransaction);

            if (creditTransaction != null) {
                String message = "" + creditTransaction.getCreditAmount() + " Amount credited to saving bank Acc No. : " + savingsBankDeposit.getAccountNumber() + "From : " + doubleScheme.getMember().getName();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "DoubleScheme Deposit Module", user.getId());
            }
            if(creditTransaction != null){
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credit from society share bank Acc No. " + creditTransaction.getId() + " submitted",
                        "DOUBLE SCHEME REFUNDED", user.getId());
            }
        }
    }

    @PostMapping("/DOUBLE_SCHEME/refund/pre-mature")
    public ResponseEntity<DoubleScheme> refundPreMatureDoubleScheme(@RequestBody DepositReportDto depositReportDto) throws ParseException {
        bodDateService.checkBOD();
        Optional<DoubleScheme> persistedDeposit = doubleSchemeService.getDoubleSchemeByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("DoubleScheme not found or not approved");
        }
        DoubleScheme doubleScheme=persistedDeposit.get();
        DoubleScheme persisted = viewADSPrematureDetails(doubleScheme,depositReportDto);
        return new ResponseEntity<>(persisted,HttpStatus.OK);
    }

    private DoubleScheme viewADSPrematureDetails(DoubleScheme doubleScheme,DepositReportDto depositReportDto){
        Date depositDate = doubleScheme.getCreatedOn();
        Date todayDate = getTodayDate();
        double days = calculateDaysBetweenDate(depositDate,todayDate);
        double years = days/365;
        BigDecimal periodOfDeposit = new BigDecimal(years);
        BigDecimal interest = calculateInterest(doubleScheme.getDepositAmount(),periodOfDeposit,doubleScheme.getRateOfInterest());
        BigDecimal maturityAmount = calculateMaturityAmount(doubleScheme.getDepositAmount(),interest);
        doubleScheme.setPreMatureInterestAmount(interest);
        doubleScheme.setPreMatureFine(depositReportDto.getPreMatureFine());
        doubleScheme.setPreMatureAmount(maturityAmount);
        doubleScheme.setPreMaturePeriodOfDeposit(periodOfDeposit);
        doubleScheme.setPreMatureDate(todayDate);
        doubleScheme.setPreMatureRateOfInterest(doubleScheme.getRateOfInterest());
        LocalDate fromDate = depositDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = (Period) Period.between(fromDate, toDate);
        String duration = diff.getYears()+" years,"+diff.getMonths()+" months, and "+diff.getDays()+" days old";
        doubleScheme.setDuration(duration);
        return doubleScheme;
    }

    @PostMapping("/DOUBLE_SCHEME/refund/pre-mature/fine-calculation")
    public ResponseEntity<DoubleScheme> refundPreMatureFineCalculation(@RequestBody DepositReportDto depositReportDto) throws ParseException {
        bodDateService.checkBOD();
        Optional<DoubleScheme> persistedDeposit = doubleSchemeService.getDoubleSchemeByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("DoubleScheme not found or not approved");
        }
        DoubleScheme doubleScheme=persistedDeposit.get();
        DoubleScheme persisted = savePrematureDetails(doubleScheme,depositReportDto);
        return new ResponseEntity<>(persisted,HttpStatus.OK);
    }

    private DoubleScheme savePrematureDetails(DoubleScheme doubleScheme,DepositReportDto depositReportDto){
        Date depositDate = doubleScheme.getCreatedOn();
        Date todayDate = getTodayDate();
        double days = calculateDaysBetweenDate(depositDate,todayDate);
        double years = days/365;
        BigDecimal periodOfDeposit = new BigDecimal(years);
        BigDecimal interest = calculateInterest(doubleScheme.getDepositAmount(),periodOfDeposit,doubleScheme.getRateOfInterest());
        BigDecimal maturityAmount = calculateMaturityAmount(doubleScheme.getDepositAmount(),interest);
        doubleScheme.setPreMatureInterestAmount(interest);
        doubleScheme.setPreMatureFine(depositReportDto.getPreMatureFine());
        doubleScheme.setPreMatureAmount(maturityAmount.subtract(depositReportDto.preMatureFine));
        doubleScheme.setPreMaturePeriodOfDeposit(periodOfDeposit);
        doubleScheme.setPreMatureDate(todayDate);
        doubleScheme.setPreMatureRateOfInterest(doubleScheme.getRateOfInterest());
        LocalDate fromDate = depositDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = (Period) Period.between(fromDate, toDate);
        String duration = "Difference is "+diff.getYears()+" years,"+diff.getMonths()+" months, and "+diff.getDays()+" days old";
        doubleScheme.setDuration(duration);
        doubleScheme.setMaturityAmount(maturityAmount.subtract(depositReportDto.preMatureFine));
        DoubleScheme persisted = doubleSchemeService.saveDoubleSchemeDeposit(doubleScheme);
        return persisted;
    }
}
