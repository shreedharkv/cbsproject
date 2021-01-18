package com.dfq.coeffi.cbs.deposit.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLog;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.Dto.DepositApprovalDto;
import com.dfq.coeffi.cbs.deposit.Dto.DepositNomineeDto;
import com.dfq.coeffi.cbs.deposit.depositTransaction.recurringDepositTransaction.RecurringDepositTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.recurringDepositTransaction.RecurringDepositTransactionService;
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
import com.dfq.coeffi.cbs.utils.DepositInterestCalculation;
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
import java.util.*;

import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.CREDIT;
import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.DEBIT;
import static com.dfq.coeffi.cbs.utils.DateUtil.calculateDaysBetweenDate;
import static com.dfq.coeffi.cbs.utils.DateUtil.getTodayDate;

@Slf4j
@RestController
public class RecurringDepositApi extends BaseController {

    private final RecurringDepositService recurringDepositService;
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
    private RecurringDepositTransactionService recurringDepositTransactionService;

    @Autowired
    private RecurringDepositApi(RecurringDepositService recurringDepositService, DepositsApprovalService depositsApprovalService,
                                MemberService memberService, ApplicationLogService applicationLogService, DepositTransactionService depositTransactionService,
                                final BankService bankService, SavingBankTransactionService savingBankTransactionService,
                                final AccountFormatService accountFormatService) {
        this.recurringDepositService = recurringDepositService;
        this.depositsApprovalService = depositsApprovalService;
        this.memberService = memberService;
        this.applicationLogService = applicationLogService;
        this.depositTransactionService = depositTransactionService;
        this.bankService = bankService;
        this.savingBankTransactionService = savingBankTransactionService;
        this.accountFormatService = accountFormatService;
    }

    @PostMapping("/recurring-deposit/unapprove-list")
    public ResponseEntity<List<RecurringDeposit>> getAllRecurringDeposits(@RequestBody DepositApprovalDto depositApprovalDto) {
        List<RecurringDeposit> recurringDepositList = recurringDepositService.getAllRecurringDeposit(depositApprovalDto.dateFrom,depositApprovalDto.dateTo);
        if (CollectionUtils.isEmpty(recurringDepositList)) {
            throw new EntityNotFoundException("recurringDepositList");
        }
        return new ResponseEntity<>(recurringDepositList,HttpStatus.OK);
    }

    @PostMapping("/recurring-deposit")
    public ResponseEntity<RecurringDeposit> createRecurringDeposit(@Valid @RequestBody final RecurringDeposit recurringDeposit,Principal principal) {
        bodDateService.checkBOD();
        Ledger ledger = accountHeadService.getLedgerByName("Recurring Deposit");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head and ledger not found");
        }

        AccountFormat accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.DEPOSIT, "RECURRING_DEPOSIT");
        if (accountFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate account number");
        }
        String accountNumber = accountFormat.getPrefix() + "-" + (accountFormat.getFromAccountNumber() + 1);

        NumberFormat numberFormat = memberService.getNumberFormatByType("Deposit_Receipt_Number");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate receipt number");
        }
        String receiptNumber = numberFormat.getPrefix() + "-" + (numberFormat.getReceiptNumber() + 1);

        Optional<Member> memberObj = memberService.getMember(recurringDeposit.getMember().getId());
        if (!memberObj.isPresent()) {
            log.warn("Unable to find member with ID : {} not found",recurringDeposit.getMember().getId());
            throw new EntityNotFoundException(Member.class.getName());
        }
        Member member = memberObj.get();
        if(member.getMemberType() == MemberType.NOMINAL){
            throw new EntityNotFoundException("Selected Member is nominal member, can not open Recurring Deposit");
        }
        recurringDeposit.setMember(member);

        savingsBankDepositService.checkSavingBankAccount(member);
        double interestAmount = DepositInterestCalculation.calculateRecurringDepositInterest(recurringDeposit.getDepositAmount(),recurringDeposit.getNumberOfInstallments(),recurringDeposit.getRateOfInterest());
        BigDecimal interest = new BigDecimal(interestAmount);
        double maturityAmount = DepositInterestCalculation.calculateRecurringDepositMaturityAmount(recurringDeposit.getDepositAmount(),recurringDeposit.getNumberOfInstallments(),interestAmount);
        recurringDeposit.setApproved(false);
        recurringDeposit.setTransactionType("CREDIT");
        recurringDeposit.setVoucherType("RECEIPT");
        recurringDeposit.setTransactionBy(getLoggedUser(principal));
        Optional<DepositAccountNumberMaster> accountNumberMaster = depositAccountNumberMasterService.getDepositAccountNumberMasterById(1);
//        recurringDeposit.setAccountNumber(accountNumberMaster.get().getAccountNumberRD());
        recurringDeposit.setApplicationNumber(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberRD())+defaultApplicationNumber));
        accountNumberMaster.get().setAccountNumberRD(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberRD()) + 1));
        depositAccountNumberMasterService.saveDepositAccountNumberMaster(accountNumberMaster.get());
        recurringDeposit.setInterestAmount(interest);
        BigDecimal totalMaturityAmount = new BigDecimal(maturityAmount);
        recurringDeposit.setMaturityAmount(totalMaturityAmount);
        recurringDeposit.setMaturityDate(DateUtil.addMonthsToDate(recurringDeposit.getNumberOfInstallments().intValue()));
        recurringDeposit.setAccountHead(ledger.getAccountHead());
        recurringDeposit.setLedger(ledger);
        recurringDeposit.setAccountNumber(accountNumber);
        recurringDeposit.setReceiptNumber(receiptNumber);
        recurringDeposit.setBalance(recurringDeposit.getDepositAmount());

        RecurringDeposit persistRecurringDeposit = recurringDepositService.saveRecurringDeposit(recurringDeposit);

        if (persistRecurringDeposit != null) {

            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"RECURRING DEPOSIT ACCOUNT CREATED","RECURRING DEPOSIT ACCOUNT CREATED POST METHOD",loggedUser.getId());
            accountFormat.setFromAccountNumber(accountFormat.getFromAccountNumber() + 1);
            accountFormatService.saveAccountNumber(accountFormat);

            numberFormat.setReceiptNumber(numberFormat.getReceiptNumber() + 1);
            memberService.updateNumberFormat(numberFormat);
        }
        return new ResponseEntity<>(persistRecurringDeposit,HttpStatus.CREATED);
    }

    @PostMapping("/recurring-deposit/approval")
    public ResponseEntity<RecurringDeposit> currentAccountApproval(@RequestBody DepositApprovalDto depositApprovalDto,Principal principal) {
        DepositsApproval depositsApproval = saveDepositsApproval(principal);
        Optional<RecurringDeposit> persistedRecurringDeposit = null;
        for (long id : depositApprovalDto.ids) {
            persistedRecurringDeposit = recurringDepositService.getRecurringDepositById(id);
            if (!persistedRecurringDeposit.isPresent()) {
                throw new EntityNotFoundException(RecurringDeposit.class.getSimpleName());
            }
            persistedRecurringDeposit.get().setDepositsApproval(depositsApproval);
            persistedRecurringDeposit.get().setStatus(true);
            persistedRecurringDeposit.get().setWithDrawn(false);
            RecurringDeposit recurringDeposit = recurringDepositService.saveRecurringDeposit(persistedRecurringDeposit.get());
            DepositTransaction depositTransaction = saveDepositTransaction(persistedRecurringDeposit.get());
            if (recurringDeposit != null) {
                transactionCreditEntry(recurringDeposit);
                recurringDepositTransactionEntry(recurringDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(recurringDeposit.getDepositAmount(),"CREDIT");
                User loggedUser = getLoggedUser(principal);
                ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"RECURRING DEPOSIT ACCOUNT APPROVED","RECURRING DEPOSIT ACCOUNT APPROVAL POST METHOD",loggedUser.getId());
            }
        }
        return new ResponseEntity<>(persistedRecurringDeposit.get(),HttpStatus.OK);
    }

    public DepositsApproval saveDepositsApproval(Principal principal) {
        DepositsApproval depositsApproval = new DepositsApproval();
        depositsApproval.setApproved(true);
        depositsApproval.setRemarks(depositsApproval.getRemarks());
        depositsApproval.setApprovedBy(getLoggedUser(principal).getFirstName());
        depositsApprovalService.saveDepositsApproval(depositsApproval);
        return depositsApproval;
    }

    @PostMapping("/RECURRING_DEPOSIT/add-nominee/{id}")
    public ResponseEntity<RecurringDeposit> addNomineeDetails(@PathVariable long id, @Valid @RequestBody DepositNomineeDto depositNomineeDto) {
        Optional<RecurringDeposit> persistedDeposit = recurringDepositService.getRecurringDepositById(id);
        RecurringDeposit recurringDeposit = persistedDeposit.get();
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException(RecurringDeposit.class.getSimpleName());
        }
        if(recurringDeposit.getDepositNomineeDetailsTwo() == null){
            recurringDeposit.setDepositNomineeDetailsTwo(depositNomineeDto.getDepositNomineeDetailsTwo());
        }else if(recurringDeposit.getDepositNomineeDetailsThree() == null){
            recurringDeposit.setDepositNomineeDetailsThree(depositNomineeDto.getDepositNomineeDetailsThree());
        }
        RecurringDeposit saveRecurringDeposit = recurringDepositService.saveRecurringDeposit(recurringDeposit);
        return new ResponseEntity<>(saveRecurringDeposit,HttpStatus.OK);
    }

    @PutMapping("/recurring-deposit/{id}")
    public ResponseEntity<RecurringDeposit> updateRecurringDeposit(@PathVariable long id, @Valid @RequestBody RecurringDeposit recurringDeposit, Principal principal) {
        Optional<RecurringDeposit> pigmyDepositById = recurringDepositService.getRecurringDepositById(id);
        if (!pigmyDepositById.isPresent()) {
            throw new EntityNotFoundException(RecurringDeposit.class.getSimpleName());
        }
        recurringDeposit.setId(id);
        recurringDeposit.setCreatedOn(pigmyDepositById.get().getCreatedOn());
        RecurringDeposit saveRecurringDeposit= recurringDepositService.saveRecurringDeposit(recurringDeposit);
        if(saveRecurringDeposit != null){
            User loggedUser = getLoggedUser(principal);
            ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"FIXED DEPOSIT ACCOUNT Modified","FIXED DEPOSIT ACCOUNT Modified PUT METHOD",loggedUser.getId());
        }
        return new ResponseEntity<>(saveRecurringDeposit,HttpStatus.OK);
    }

    @PostMapping("recurring-deposit/account-number")
    public ResponseEntity<RecurringDeposit> getRecurringDepositByAccountNumber(@RequestBody DepositReportDto depositReportDto) {
        Optional<RecurringDeposit> persistedDeposit = recurringDepositService.getRecurringDepositByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("No Active Deposits found for the account number: "+depositReportDto.accountNumber);
        }
        return new ResponseEntity(persistedDeposit.get(),HttpStatus.OK);
    }

    @PostMapping("/RECURRING_DEPOSIT/refund")
    public ResponseEntity<RecurringDeposit> refundRecurringDeposit(@RequestBody DepositReportDto depositReportDto,Principal principal) {
        bodDateService.checkBOD();
        Optional<RecurringDeposit> recurringDeposit = recurringDepositService.getRecurringDepositByAccountNumber(depositReportDto.accountNumber);
        if (!recurringDeposit.isPresent()) {
            throw new EntityNotFoundException(RecurringDeposit.class.getSimpleName());
        }
        recurringDepositService.checkRecurringDepositForLoan(recurringDeposit.get().getAccountNumber());

        recurringDeposit.get().setId(recurringDeposit.get().getId());
        recurringDeposit.get().setWithDrawn(true);
        recurringDeposit.get().setAccountClosedOn(DateUtil.getTodayDate());

        recurringDeposit.get().setModeOfPayment(depositReportDto.getModeOfPayment());
        RecurringDeposit deposit = recurringDepositService.saveRecurringDeposit(recurringDeposit.get());
        if(depositReportDto.getModeOfPayment().equalsIgnoreCase("Cash")){
            if(deposit!=null) {
                transactionDebitEntry(deposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(deposit.getMaturityAmount(),"DEBIT");
            }
        }
        else if(depositReportDto.getModeOfPayment().equalsIgnoreCase("Bank")){
            if(deposit!=null) {
                transactionDebitEntry(deposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(deposit.getMaturityAmount(),"DEBIT");
            }
        }
        else if(depositReportDto.getModeOfPayment().equalsIgnoreCase("Transfer")){
            if(deposit!=null) {
                User loggedUser = getLoggedUser(principal);
                transactionDebitEntry(deposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(deposit.getMaturityAmount(),"DEBIT");
                depositRefundCreditTransactionEntry(deposit,loggedUser);
            }
        }
        if(deposit != null){
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"Recurring deposit refunded Account number"+deposit.getAccountNumber(),"Recurring Deposit refunded",loggedUser.getId());
        }
        return new ResponseEntity<>(recurringDeposit.get(),HttpStatus.OK);
    }

    public DepositTransaction saveDepositTransaction(RecurringDeposit recurringDeposit) {
        DepositTransaction depositTransaction = new DepositTransaction();
        depositTransaction.setAccountNumber(recurringDeposit.getAccountNumber());
        depositTransaction.setDepositType(recurringDeposit.getDepositType());
        depositTransaction.setDepositAmount(recurringDeposit.getDepositAmount());
        depositTransaction.setInterestAmount(recurringDeposit.getInterestAmount());
        depositTransaction.setMember(recurringDeposit.getMember());
        depositTransaction.setTransactionType(TransactionType.CREDIT);
        depositTransaction.setBalanceAmount(recurringDeposit.getMaturityAmount());
        depositTransactionService.createDepositTransactionService(depositTransaction);
        return depositTransaction;
    }

    public DepositTransaction withDraw(RecurringDeposit recurringDeposit) {
        DepositTransaction depositTransaction = new DepositTransaction();
        depositTransaction.setAccountNumber(recurringDeposit.getAccountNumber());
        depositTransaction.setDepositType(recurringDeposit.getDepositType());
        depositTransaction.setMember(recurringDeposit.getMember());
        depositTransaction.setTransactionType(TransactionType.DEBIT);
        depositTransaction.setWithDrawAmount(recurringDeposit.getMaturityAmount());
        depositTransactionService.createDepositTransactionService(depositTransaction);
        return depositTransaction;
    }

    @PostMapping("recurring-deposit/calculation")
    public ArrayList getCalcRecurringDeposit(@RequestBody RecurringDeposit recurringDeposit){
        ArrayList al = new ArrayList();
        double interestAmount = DepositInterestCalculation.calculateRecurringDepositInterest(recurringDeposit.getDepositAmount(),recurringDeposit.getNumberOfInstallments(),recurringDeposit.getRateOfInterest());
        BigDecimal interest = new BigDecimal(interestAmount);
        double maturityAmount = DepositInterestCalculation.calculateRecurringDepositMaturityAmount(recurringDeposit.getDepositAmount(),recurringDeposit.getNumberOfInstallments(),interestAmount);
        Date maturityDate = DateUtil.addMonthsToDate(recurringDeposit.getNumberOfInstallments().intValue());
        double principleAmount = maturityAmount - interestAmount;
        al.add(maturityAmount);
        al.add(interest);
        al.add(DateUtil.convertToDateString(maturityDate));
        al.add(principleAmount);
        return al;
    }

    @PostMapping("/RECURRING_DEPOSIT/by-customer-id")
    public ResponseEntity<List<RecurringDeposit>> recurringDepositByCustomerId(@RequestBody DepositReportDto depositReportDto) {
        List<RecurringDeposit> recurringDeposits = recurringDepositService.getRecurringDepositByCustomerId(depositReportDto.customerId);
        if (recurringDeposits.isEmpty()) {
            throw new EntityNotFoundException(FixedDeposit.class.getSimpleName());
        }
        return new ResponseEntity<>(recurringDeposits,HttpStatus.OK);
    }

    private void transactionCreditEntry(RecurringDeposit recurringDeposit) {
        Ledger ledger = accountHeadService.getLedgerByName("Recurring Deposit");
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Initial Amount Credited for recurringDeposit Account " + recurringDeposit.getId());
            creditTransaction.setCreditAmount(recurringDeposit.getDepositAmount());
            creditTransaction.setTransactionBy(recurringDeposit.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setTransferType("Cash");
            creditTransaction.setParticulars("FIXED DEPOSIT");
            creditTransaction.setOtherFees(BigDecimal.ZERO);
            creditTransaction.setTransactionType(recurringDeposit.getTransactionType());
            creditTransaction.setVoucherType(recurringDeposit.getVoucherType());
            creditTransaction.setAccountName(recurringDeposit.getMember().getName() + " (" + recurringDeposit.getMember().getMemberNumber() + ")");

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName,CREDIT);
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountNumber(recurringDeposit.getAccountNumber());
            Transaction persistedDebitTransaction = transactionService.transactionEntry(creditTransaction);
        }
    }

    private void transactionDebitEntry(RecurringDeposit recurringDeposit) {
        Ledger ledger = accountHeadService.getLedgerByName("Recurring Deposit");
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(recurringDeposit.getMaturityAmount());
            debitTransaction.setRemark("Amount debited for RECURRING DEPOPSIT Withdrawn " + recurringDeposit.getId());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(recurringDeposit.getTransactionBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setParticulars("RECURRING DEPOSIT");
            debitTransaction.setVoucherType(recurringDeposit.getVoucherType());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName,DEBIT);
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountNumber(recurringDeposit.getAccountNumber());
            debitTransaction.setTransactionBy(recurringDeposit.getTransactionBy());
            debitTransaction.setTransferType(recurringDeposit.getModeOfPayment());
            debitTransaction.setAccountName(recurringDeposit.getMember().getName() + " (" + recurringDeposit.getMember().getMemberNumber() + ")");

            Transaction persistedDebitTransaction = transactionService.transactionEntry(debitTransaction);
        }
    }

    private void recurringDepositTransactionEntry(RecurringDeposit recurringDeposit){
        RecurringDepositTransaction creditTransaction = new RecurringDepositTransaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Initial recurringDeposit Account created" + recurringDeposit.getAccountNumber());
            creditTransaction.setCreditAmount(new BigDecimal(0));
            creditTransaction.setTransactionBy(recurringDeposit.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setTransactionType(recurringDeposit.getTransactionType());
            creditTransaction.setVoucherType(recurringDeposit.getVoucherType());
            creditTransaction.setBalance(new BigDecimal(0));
            creditTransaction.setAccountNumber(recurringDeposit.getAccountNumber());
            creditTransaction.setRecurringDeposit(recurringDeposit);
            creditTransaction.setDepositAmount(recurringDeposit.getDepositAmount());
            creditTransaction.setBalance(recurringDeposit.getDepositAmount());
            creditTransaction.setTotalPrincipleAmount(recurringDeposit.getDepositAmount().multiply(recurringDeposit.getNumberOfInstallments()));
            recurringDepositTransactionService.createRecurringDepositTransaction(creditTransaction);
    }

    private void depositRefundCreditTransactionEntry(RecurringDeposit recurringDeposit, User user){

        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(recurringDeposit.getMember().getMemberNumber());

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
            depositBalance = depositBalance.add(recurringDeposit.getMaturityAmount());

            SavingBankTransaction sbTransaction = new SavingBankTransaction();

            sbTransaction.setTransactionType("CREDIT");
            sbTransaction.setCreditAmount(recurringDeposit.getMaturityAmount());

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
            transactionUtil.getUpdateSocietyBalance(recurringDeposit.getMaturityAmount(),"CREDIT");
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For debit transaction against the refund share
            Transaction creditTransaction = new Transaction();

            creditTransaction.setRemark("Amount credited to Saving Bank Acc No. : " + savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setCreditAmount(recurringDeposit.getMaturityAmount());
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setOtherFees(BigDecimal.ZERO);
            creditTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransferType(recurringDeposit.getModeOfPayment());
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountName(recurringDeposit.getMember().getName() + " (" + recurringDeposit.getMember().getMemberNumber() + ")");


            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(recurringDeposit.getMaturityAmount());
            creditTransaction.setBalance(balance);

            transactionService.transactionEntry(creditTransaction);

            if (creditTransaction != null) {
                String message = "" + creditTransaction.getCreditAmount() + " Amount credited to saving bank Acc No. : " + savingsBankDeposit.getAccountNumber() + "From : " + recurringDeposit.getMember().getName();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "RecurringDeposit Deposit Module", user.getId());
            }
            if(creditTransaction != null){
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credit from society share bank Acc No. " + creditTransaction.getId() + " submitted",
                        "RECURRING DEPOSIT REFUNDED", user.getId());
            }
        }
    }

    @PostMapping("/RECURRING_DEPOSIT/refund/pre-mature")
    public ResponseEntity<RecurringDeposit> refundPreMatureRecurringDeposit(@RequestBody DepositReportDto depositReportDto) throws ParseException {
        bodDateService.checkBOD();
        Optional<RecurringDeposit> persistedDeposit = recurringDepositService.getRecurringDepositByAccountNumber(depositReportDto.accountNumber);
        recurringDepositService.checkRecurringDepositForLoan(persistedDeposit.get().getAccountNumber());
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("RecurringDeposit not found or not approved");
        }
        RecurringDeposit recurringDeposit =persistedDeposit.get();
        RecurringDeposit persisted = viewPrematureDetails(recurringDeposit,depositReportDto);
        return new ResponseEntity<>(persisted,HttpStatus.OK);
    }

    private RecurringDeposit viewPrematureDetails(RecurringDeposit recurringDeposit,DepositReportDto depositReportDto){
        Date depositDate = recurringDeposit.getCreatedOn();
        Date todayDate = getTodayDate();
        double days = calculateDaysBetweenDate(depositDate,todayDate);
        double years = days/365;
        BigDecimal periodOfDeposit = new BigDecimal(years);
        double maturityAmount = 0;
        double interestAmount = 0;
        if(recurringDeposit.getBalance() == null ) {
            throw new EntityNotFoundException("Balance is not present");
        }
        BigDecimal numberOfInstallments=recurringDeposit.getBalance().divide(recurringDeposit.getDepositAmount());
        interestAmount = DepositInterestCalculation.calculateRecurringDepositInterest(recurringDeposit.getDepositAmount(), numberOfInstallments, recurringDeposit.getRateOfInterest());
        maturityAmount = DepositInterestCalculation.calculateRecurringDepositMaturityAmount(recurringDeposit.getDepositAmount(), numberOfInstallments, interestAmount);
        BigDecimal interest = new BigDecimal(interestAmount);
        BigDecimal maturityAmt = new BigDecimal(maturityAmount);
        recurringDeposit.setPreMatureInterestAmount(interest);
        recurringDeposit.setPreMatureFine(depositReportDto.getPreMatureFine());
        recurringDeposit.setPreMatureAmount(maturityAmt);
        recurringDeposit.setPaidInstallments(numberOfInstallments);
        recurringDeposit.setPreMatureDate(todayDate);
        recurringDeposit.setPreMatureRateOfInterest(recurringDeposit.getRateOfInterest());
        LocalDate fromDate = depositDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = (Period) Period.between(fromDate, toDate);
        String duration = diff.getYears()+" years,"+diff.getMonths()+" months, and "+diff.getDays()+" days old";
        recurringDeposit.setDuration(duration);
        return recurringDeposit;
    }

    @PostMapping("/RECURRING_DEPOSIT/refund/pre-mature/fine-calculation")
    public ResponseEntity<RecurringDeposit> refundPreMatureFineCalculation(@RequestBody DepositReportDto depositReportDto) throws ParseException {
        bodDateService.checkBOD();
        Optional<RecurringDeposit> persistedDeposit = recurringDepositService.getRecurringDepositByAccountNumber(depositReportDto.accountNumber);
        recurringDepositService.checkRecurringDepositForLoan(persistedDeposit.get().getAccountNumber());
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("RecurringDeposit not found or not approved");
        }
        RecurringDeposit recurringDeposit =persistedDeposit.get();
        RecurringDeposit persisted = savePrematureDetails(recurringDeposit,depositReportDto);
        return new ResponseEntity<>(persisted,HttpStatus.OK);
    }

    @GetMapping("recurring-deposit/transactions/{accountNumber}")
    public ResponseEntity<RecurringDepositTransaction> getRecurringDepositByAccountNumber(@PathVariable String accountNumber) {
        List<RecurringDepositTransaction> persistedDeposit = recurringDepositTransactionService.getRecurringDepositTransactionsByAccountNumber(accountNumber);
        if (persistedDeposit.isEmpty()) {
            throw new EntityNotFoundException("No RecurringDepositTransaction found for the account number: "+accountNumber);
        }
        return new ResponseEntity(persistedDeposit,HttpStatus.OK);
    }


    private RecurringDeposit savePrematureDetails(RecurringDeposit recurringDeposit,DepositReportDto depositReportDto){
        Date depositDate = recurringDeposit.getCreatedOn();
        Date todayDate = getTodayDate();
        double days = calculateDaysBetweenDate(depositDate,todayDate);
        double years = days/365;
        BigDecimal periodOfDeposit = new BigDecimal(years);
        double maturityAmount = 0;
        double interestAmount = 0;
        if(recurringDeposit.getBalance() == null ) {
            throw new EntityNotFoundException("Balance is not present");
        }
        BigDecimal numberOfInstallments=recurringDeposit.getBalance().divide(recurringDeposit.getDepositAmount());
        interestAmount = DepositInterestCalculation.calculateRecurringDepositInterest(recurringDeposit.getDepositAmount(), numberOfInstallments, recurringDeposit.getRateOfInterest());
        maturityAmount = DepositInterestCalculation.calculateRecurringDepositMaturityAmount(recurringDeposit.getDepositAmount(), numberOfInstallments, interestAmount);
        BigDecimal interest = new BigDecimal(interestAmount);
        BigDecimal maturityAmt = new BigDecimal(maturityAmount);
        recurringDeposit.setPreMatureInterestAmount(interest);
        recurringDeposit.setPreMatureFine(depositReportDto.getPreMatureFine());
        recurringDeposit.setPreMatureAmount(maturityAmt.subtract(depositReportDto.preMatureFine));
        recurringDeposit.setPaidInstallments(numberOfInstallments);
        recurringDeposit.setPreMatureDate(todayDate);
        recurringDeposit.setPreMatureRateOfInterest(recurringDeposit.getRateOfInterest());
        LocalDate fromDate = depositDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = (Period) Period.between(fromDate, toDate);
        String duration = "Difference is "+diff.getYears()+" years,"+diff.getMonths()+" months, and "+diff.getDays()+" days old";
        recurringDeposit.setDuration(duration);
        recurringDeposit.setMaturityAmount(maturityAmt.subtract(depositReportDto.preMatureFine));
        RecurringDeposit persisted = recurringDepositService.saveRecurringDeposit(recurringDeposit);
        return persisted;
    }


}