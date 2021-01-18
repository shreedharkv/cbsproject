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
public class TermDepositApi extends BaseController {

    private final TermDepositService termDepositService;
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
    private TermDepositApi(TermDepositService termDepositService, DepositsApprovalService depositsApprovalService,
                           MemberService memberService, ApplicationLogService applicationLogService,
                           DepositTransactionService depositTransactionService,
                           final BankService bankService, SavingBankTransactionService savingBankTransactionService,
                           final AccountFormatService accountFormatService) {
        this.termDepositService = termDepositService;
        this.depositsApprovalService = depositsApprovalService;
        this.memberService = memberService;
        this.applicationLogService = applicationLogService;
        this.depositTransactionService = depositTransactionService;
        this.bankService = bankService;
        this.savingBankTransactionService = savingBankTransactionService;
        this.accountFormatService = accountFormatService;
    }

    @PostMapping("/term-deposit/unapprove-list")
    public ResponseEntity<List<TermDeposit>> getAllTermDeposits(@RequestBody DepositApprovalDto depositApprovalDto) {
        List<TermDeposit> termDepositList = termDepositService.getAllTermDeposits(depositApprovalDto.dateFrom,depositApprovalDto.dateTo);
        if (CollectionUtils.isEmpty(termDepositList)) {
            throw new EntityNotFoundException("termDepositList");
        }
        return new ResponseEntity<>(termDepositList,HttpStatus.OK);
    }

    @PostMapping("/term-deposit")
    public ResponseEntity<TermDeposit> createTermDeposit(@Valid @RequestBody final TermDeposit termDeposit,Principal principal) {
        bodDateService.checkBOD();
        Ledger ledger = accountHeadService.getLedgerByName("Term Deposit");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head and ledger not found");
        }

        AccountFormat accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.DEPOSIT, "TERM_DEPOSIT");
        if (accountFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate account number");
        }
        String accountNumber = accountFormat.getPrefix() + "-" + (accountFormat.getFromAccountNumber() + 1);

        NumberFormat numberFormat = memberService.getNumberFormatByType("Deposit_Receipt_Number");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate receipt number");
        }
        String receiptNumber = numberFormat.getPrefix() + "-" + (numberFormat.getReceiptNumber() + 1);

        Optional<Member> memberObj = memberService.getMember(termDeposit.getMember().getId());
        if (!memberObj.isPresent()) {
            log.warn("Unable to find member with ID : {} not found",termDeposit.getMember().getId());
            throw new EntityNotFoundException(Member.class.getName());
        }
        Member member = memberObj.get();
        if(member.getMemberType() == MemberType.NOMINAL){
            throw new EntityNotFoundException("Selected Member is nominal member, can not open Term Deposit");
        }
        termDeposit.setMember(member);

        savingsBankDepositService.checkSavingBankAccount(member);
        BigDecimal interest = DepositInterestCalculation.calculateInterest(termDeposit.getDepositAmount(),termDeposit.getPeriodOfDeposit(),termDeposit.getRateOfInterest());
        BigDecimal maturityAmount = DepositInterestCalculation.calculateMaturityAmount(termDeposit.getDepositAmount(),interest);
        termDeposit.setApproved(false);
        termDeposit.setVoucherType("RECEIPT");
        termDeposit.setTransactionType("CREDIT");
        Optional<DepositAccountNumberMaster> accountNumberMaster = depositAccountNumberMasterService.getDepositAccountNumberMasterById(1);
//        termDeposit.setAccountNumber(accountNumberMaster.get().getAccountNumberTDR());
        termDeposit.setApplicationNumber(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberTDR())+defaultApplicationNumber));
        accountNumberMaster.get().setAccountNumberTDR(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberTDR()) + 1));
        depositAccountNumberMasterService.saveDepositAccountNumberMaster(accountNumberMaster.get());
        termDeposit.setInterestAmount(interest);
        termDeposit.setMaturityAmount(maturityAmount);
        termDeposit.setMaturityDate(DateUtil.addYearsToDate(termDeposit.getPeriodOfDeposit().intValue()));
        termDeposit.setTransactionBy(getLoggedUser(principal));
        termDeposit.setAccountHead(ledger.getAccountHead());
        termDeposit.setLedger(ledger);
        termDeposit.setAccountNumber(accountNumber);
        termDeposit.setReceiptNumber(receiptNumber);

        TermDeposit persistedDeposit = termDepositService.saveTermDeposit(termDeposit);
        if (persistedDeposit != null) {
            User loggedUser = getLoggedUser(principal);
            ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"TERM DEPOSIT ACCOUNT CREATED","TERM DEPOSIT ACCOUNT POST METHOD",loggedUser.getId());
            accountFormat.setFromAccountNumber(accountFormat.getFromAccountNumber() + 1);
            accountFormatService.saveAccountNumber(accountFormat);

            numberFormat.setReceiptNumber(numberFormat.getReceiptNumber() + 1);
            memberService.updateNumberFormat(numberFormat);
        }
        return new ResponseEntity<>(persistedDeposit,HttpStatus.CREATED);
    }

    @PostMapping("/term-deposit/approval")
    public ResponseEntity<TermDeposit> termDepositApproval(@RequestBody DepositApprovalDto depositApprovalDto,Principal principal) {
        DepositsApproval depositsApproval = saveDepositsApproval(principal);
        Optional<TermDeposit> persistedTermDeposit = null;
        for (long id : depositApprovalDto.ids) {
            persistedTermDeposit = termDepositService.getTermDepositById(id);
            if (!persistedTermDeposit.isPresent()) {
                throw new EntityNotFoundException(RecurringDeposit.class.getSimpleName());
            }
            persistedTermDeposit.get().setDepositsApproval(depositsApproval);
            persistedTermDeposit.get().setStatus(true);
            persistedTermDeposit.get().setWithDrawn(false);
            TermDeposit termDeposit = termDepositService.saveTermDeposit(persistedTermDeposit.get());
            DepositTransaction depositTransaction = saveDepositTransaction(persistedTermDeposit.get());
            if (termDeposit != null) {
                transactionCreditEntry(termDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(termDeposit.getDepositAmount(),"CREDIT");
                User loggedUser = getLoggedUser(principal);
                ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"TERM DEPOSIT ACCOUNT APPROVED","TERM DEPOSIT APPROVAL POST METHOD",loggedUser.getId());
            }
        }
        return new ResponseEntity<>(persistedTermDeposit.get(),HttpStatus.OK);
    }

    public DepositsApproval saveDepositsApproval(Principal principal) {
        DepositsApproval depositsApproval = new DepositsApproval();
        depositsApproval.setApproved(true);
        depositsApproval.setRemarks(depositsApproval.getRemarks());
        depositsApproval.setApprovedBy(getLoggedUser(principal).getFirstName());
        depositsApprovalService.saveDepositsApproval(depositsApproval);
        return depositsApproval;
    }

    @PostMapping("/TERM_DEPOSIT/add-nominee/{id}")
    public ResponseEntity<TermDeposit> addNomineeDetails(@PathVariable long id, @Valid @RequestBody DepositNomineeDto depositNomineeDto) {
        Optional<TermDeposit> persistedDeposit = termDepositService.getTermDepositById(id);
        TermDeposit termDeposit = persistedDeposit.get();
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException(TermDeposit.class.getSimpleName());
        }
        if(termDeposit.getDepositNomineeDetailsTwo() == null){
            termDeposit.setDepositNomineeDetailsTwo(depositNomineeDto.getDepositNomineeDetailsTwo());
        }else if(termDeposit.getDepositNomineeDetailsThree() == null){
            termDeposit.setDepositNomineeDetailsThree(depositNomineeDto.getDepositNomineeDetailsThree());
        }
        TermDeposit saveTermDeposit = termDepositService.saveTermDeposit(termDeposit);
        return new ResponseEntity<>(saveTermDeposit,HttpStatus.OK);
    }

    @PutMapping("/term-deposit/{id}")
    public ResponseEntity<TermDeposit> updateTermDeposit(@PathVariable long id, @Valid @RequestBody TermDeposit termDeposit, Principal principal) {
        Optional<TermDeposit> termDepositById = termDepositService.getTermDepositById(id);
        if (!termDepositById.isPresent()) {
            throw new EntityNotFoundException(TermDeposit.class.getSimpleName());
        }
        termDeposit.setId(id);
        termDeposit.setCreatedOn(termDepositById.get().getCreatedOn());
        TermDeposit saveTermDeposit = termDepositService.saveTermDeposit(termDeposit);
        if(saveTermDeposit != null){
            User loggedUser = getLoggedUser(principal);
            ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"FIXED DEPOSIT ACCOUNT Modified","FIXED DEPOSIT ACCOUNT Modified PUT METHOD",loggedUser.getId());
        }
        return new ResponseEntity<>(saveTermDeposit,HttpStatus.OK);
    }

    @PostMapping("term-deposit/account-number")
    public ResponseEntity<TermDeposit> getTermDepositByAccountNumber(@RequestBody DepositReportDto depositReportDto) {
        Optional<TermDeposit> persistedDeposit = termDepositService.getTermDepositByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("No Active Deposits found for the account number: "+depositReportDto.accountNumber);
        }
        return new ResponseEntity(persistedDeposit.get(),HttpStatus.OK);
    }

    @PostMapping("/TERM_DEPOSIT/refund")
    public ResponseEntity<TermDeposit> refundTermDeposit(@RequestBody DepositReportDto depositReportDto,Principal principal) {
        bodDateService.checkBOD();
        Optional<TermDeposit> termDeposit = termDepositService.getTermDepositByAccountNumber(depositReportDto.accountNumber);
        if (!termDeposit.isPresent()) {
            throw new EntityNotFoundException("TermDeposit not found or not approved");
        }
        termDeposit.get().setId(termDeposit.get().getId());
        termDeposit.get().setWithDrawn(true);
        termDeposit.get().setAccountClosedOn(DateUtil.getTodayDate());

        termDeposit.get().setModeOfPayment(depositReportDto.getModeOfPayment());
        TermDeposit persistedDeposit = termDepositService.saveTermDeposit(termDeposit.get());
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
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(),"Term deposit refunded Account number"+persistedDeposit.getAccountNumber(),"Term Deposit Refunded",loggedUser.getId());
        }
        DepositTransaction depositTransaction = withDraw(termDeposit.get());
        return new ResponseEntity<>(termDeposit.get(),HttpStatus.OK);
    }

    public DepositTransaction saveDepositTransaction(TermDeposit termDeposit) {
        DepositTransaction depositTransaction = new DepositTransaction();
        depositTransaction.setAccountNumber(termDeposit.getAccountNumber());
        depositTransaction.setDepositType(termDeposit.getDepositType());
        depositTransaction.setDepositAmount(termDeposit.getDepositAmount());
        depositTransaction.setInterestAmount(termDeposit.getInterestAmount());
        depositTransaction.setMember(termDeposit.getMember());
        depositTransaction.setTransactionType(TransactionType.CREDIT);
        depositTransaction.setBalanceAmount(termDeposit.getMaturityAmount());
        depositTransactionService.createDepositTransactionService(depositTransaction);
        return depositTransaction;
    }

    public DepositTransaction withDraw(TermDeposit termDeposit) {
        DepositTransaction depositTransaction = new DepositTransaction();
        depositTransaction.setAccountNumber(termDeposit.getAccountNumber());
        depositTransaction.setDepositType(termDeposit.getDepositType());
        depositTransaction.setMember(termDeposit.getMember());
        depositTransaction.setTransactionType(TransactionType.DEBIT);
        depositTransaction.setWithDrawAmount(termDeposit.getMaturityAmount());
        depositTransactionService.createDepositTransactionService(depositTransaction);
        return depositTransaction;
    }

    private void transactionCreditEntry(TermDeposit termDeposit) {

        Ledger ledger = accountHeadService.getLedgerByName("Term Deposit");
        if(ledger == null){
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Initial Amount Credited for termDeposit Account " + termDeposit.getId());
            creditTransaction.setCreditAmount(termDeposit.getDepositAmount());
            creditTransaction.setTransactionBy(termDeposit.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setVoucherType(termDeposit.getVoucherType());
            creditTransaction.setParticulars("TERM DEPOSIT");
            creditTransaction.setTransferType("Cash");
            creditTransaction.setTransactionType(termDeposit.getTransactionType());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName,CREDIT);
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountNumber(termDeposit.getAccountNumber());
            creditTransaction.setAccountName(termDeposit.getMember().getName() + " (" + termDeposit.getMember().getMemberNumber() + ")");

            Transaction persistedDebitTransaction = transactionService.transactionEntry(creditTransaction);
        }
    }

    private void transactionDebitEntry(TermDeposit termDeposit) {
        Ledger ledger = accountHeadService.getLedgerByName("Term Deposit");
        if(ledger == null){
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(termDeposit.getMaturityAmount());
            debitTransaction.setRemark("Amount debited for TERM DEPOSIT Withdrawn " + termDeposit.getId());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(termDeposit.getTransactionBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setParticulars("TERM DEPOSIT");
            debitTransaction.setVoucherType(termDeposit.getVoucherType());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName,DEBIT);
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountNumber(termDeposit.getAccountNumber());
            debitTransaction.setTransactionBy(termDeposit.getTransactionBy());
            debitTransaction.setTransferType(termDeposit.getModeOfPayment());
            debitTransaction.setAccountName(termDeposit.getMember().getName() + " (" + termDeposit.getMember().getMemberNumber() + ")");

            Transaction persistedDebitTransaction = transactionService.transactionEntry(debitTransaction);
        }
    }

    private void depositRefundCreditTransactionEntry(TermDeposit termDeposit, User user){

        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(termDeposit.getMember().getMemberNumber());

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
            depositBalance = depositBalance.add(termDeposit.getMaturityAmount());

            SavingBankTransaction sbTransaction = new SavingBankTransaction();

            sbTransaction.setTransactionType("CREDIT");
            sbTransaction.setCreditAmount(termDeposit.getMaturityAmount());

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
            transactionUtil.getUpdateSocietyBalance(termDeposit.getMaturityAmount(),"CREDIT");
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For debit transaction against the refund share
            Transaction creditTransaction = new Transaction();

            creditTransaction.setRemark("Amount credited to Saving Bank Acc No. : " + savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setCreditAmount(termDeposit.getMaturityAmount());
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransferType(termDeposit.getModeOfPayment());
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountName(termDeposit.getMember().getName() + " (" + termDeposit.getMember().getMemberNumber() + ")");


            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(termDeposit.getMaturityAmount());
            creditTransaction.setBalance(balance);

            transactionService.transactionEntry(creditTransaction);

            if (creditTransaction != null) {
                String message = "" + creditTransaction.getCreditAmount() + " Amount credited to saving bank Acc No. : " + savingsBankDeposit.getAccountNumber() + "From : " + termDeposit.getMember().getName();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "TermDeposit Module", user.getId());
            }
            if(creditTransaction != null){
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credit from society share bank Acc No. " + creditTransaction.getId() + " submitted",
                        "TERM DEPOSIT REFUNDED", user.getId());
            }
        }
    }

    @PostMapping("/TERM_DEPOSIT/refund/pre-mature")
    public ResponseEntity<TermDeposit> refundPreMatureTermDeposit(@RequestBody DepositReportDto depositReportDto) throws ParseException {
        bodDateService.checkBOD();
        Optional<TermDeposit> persistedDeposit = termDepositService.getTermDepositByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("TermDeposit not found or not approved");
        }
        TermDeposit termDeposit=persistedDeposit.get();
        TermDeposit persisted = viewPrematureDetails(termDeposit,depositReportDto);
        return new ResponseEntity<>(persisted,HttpStatus.OK);
    }

    private TermDeposit viewPrematureDetails(TermDeposit termDeposit,DepositReportDto depositReportDto){
        Date depositDate = termDeposit.getCreatedOn();
        Date todayDate = getTodayDate();
        double days = calculateDaysBetweenDate(depositDate,todayDate);
        double years = days/365;
        BigDecimal periodOfDeposit = new BigDecimal(years);
        BigDecimal interest = calculateInterest(termDeposit.getDepositAmount(),periodOfDeposit,termDeposit.getRateOfInterest());
        BigDecimal maturityAmount = calculateMaturityAmount(termDeposit.getDepositAmount(),interest);
        termDeposit.setPreMatureInterestAmount(interest);
        termDeposit.setPreMatureFine(depositReportDto.getPreMatureFine());
        termDeposit.setPreMatureAmount(maturityAmount);
        termDeposit.setPreMaturePeriodOfDeposit(periodOfDeposit);
        termDeposit.setPreMatureDate(todayDate);
        termDeposit.setPreMatureRateOfInterest(termDeposit.getRateOfInterest());
        LocalDate fromDate = depositDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = (Period) Period.between(fromDate, toDate);
        String duration = "Difference is "+diff.getYears()+" years,"+diff.getMonths()+" months, and "+diff.getDays()+" days old";
        termDeposit.setDuration(duration);
        return termDeposit;
    }

    @PostMapping("/TERM_DEPOSIT/refund/pre-mature/fine-calculation")
    public ResponseEntity<TermDeposit> refundPreMatureFineCalculation(@RequestBody DepositReportDto depositReportDto) throws ParseException {
        bodDateService.checkBOD();
        Optional<TermDeposit> persistedDeposit = termDepositService.getTermDepositByAccountNumber(depositReportDto.accountNumber);
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException("TermDeposit not found or not approved");
        }
        TermDeposit termDeposit=persistedDeposit.get();
        TermDeposit persisted = savePrematureDetails(termDeposit,depositReportDto);
        return new ResponseEntity<>(persisted,HttpStatus.OK);
    }

    private TermDeposit savePrematureDetails(TermDeposit termDeposit,DepositReportDto depositReportDto){
        Date depositDate = termDeposit.getCreatedOn();
        Date todayDate = getTodayDate();
        double days = calculateDaysBetweenDate(depositDate,todayDate);
        double years = days/365;
        BigDecimal periodOfDeposit = new BigDecimal(years);
        BigDecimal interest = calculateInterest(termDeposit.getDepositAmount(),periodOfDeposit,termDeposit.getRateOfInterest());
        BigDecimal maturityAmount = calculateMaturityAmount(termDeposit.getDepositAmount(),interest);
        termDeposit.setPreMatureInterestAmount(interest);
        termDeposit.setPreMatureFine(depositReportDto.getPreMatureFine());
        termDeposit.setPreMatureAmount(maturityAmount.subtract(depositReportDto.preMatureFine));
        termDeposit.setPreMaturePeriodOfDeposit(periodOfDeposit);
        termDeposit.setPreMatureDate(todayDate);
        termDeposit.setPreMatureRateOfInterest(termDeposit.getRateOfInterest());
        LocalDate fromDate = depositDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = (Period) Period.between(fromDate, toDate);
        String duration = diff.getYears()+" years,"+diff.getMonths()+" months, and "+diff.getDays()+" days old";
        termDeposit.setDuration(duration);
        termDeposit.setMaturityAmount(maturityAmount.subtract(depositReportDto.preMatureFine));
        TermDeposit persisted = termDepositService.saveTermDeposit(termDeposit);
        return persisted;
    }
}