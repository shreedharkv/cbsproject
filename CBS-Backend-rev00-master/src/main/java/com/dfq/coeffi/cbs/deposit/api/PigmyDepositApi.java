package com.dfq.coeffi.cbs.deposit.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLog;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.Dto.DepositApprovalDto;
import com.dfq.coeffi.cbs.deposit.Dto.DepositNomineeDto;
import com.dfq.coeffi.cbs.deposit.Dto.PigmyDepositDto;
import com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction.PigmyDepositTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction.PigmyDepositTransactionService;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.CREDIT;
import static com.dfq.coeffi.cbs.utils.EmiCalculator.pigmyInterestCalculation;

@Slf4j
@RestController
public class PigmyDepositApi extends BaseController {

    private final PigmyDepositService pigmyDepositService;
    private final DepositsApprovalService depositsApprovalService;
    private final MemberService memberService;
    private final ApplicationLogService applicationLogService;
    public final BankService bankService;
    public final PigmyDepositTransactionService pigmyDepositTransactionService;
    public final SavingBankTransactionService savingBankTransactionService;
    public final AccountFormatService accountFormatService;
    long defaultApplicationNumber = 201900000;

    @Autowired
    private DepositAccountNumberMasterService depositAccountNumberMasterService;

    @Autowired
    private BODDateService bodDateService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountHeadService accountHeadService;

    @Autowired
    private SavingsBankDepositService savingsBankDepositService;

    @Autowired
    private PigmyAgentService pigmyAgentService;

    @Autowired
    private PigmyDepositApi(PigmyDepositService pigmyDepositService, DepositsApprovalService depositsApprovalService,
                            MemberService memberService, ApplicationLogService applicationLogService,
                            final BankService bankService, final PigmyDepositTransactionService pigmyDepositTransactionService,
                            SavingBankTransactionService savingBankTransactionService, final AccountFormatService accountFormatService) {
        this.pigmyDepositService = pigmyDepositService;
        this.depositsApprovalService = depositsApprovalService;
        this.memberService = memberService;
        this.applicationLogService = applicationLogService;
        this.bankService = bankService;
        this.pigmyDepositTransactionService = pigmyDepositTransactionService;
        this.savingBankTransactionService = savingBankTransactionService;
        this.accountFormatService = accountFormatService;
    }

    @PostMapping("/pigmy-deposit/unapprove-list")
    public ResponseEntity<List<PigmyDeposit>> getAllPigmyDeposits(@RequestBody DepositApprovalDto depositApprovalDto) {
        List<PigmyDeposit> pigmyDeposits = pigmyDepositService.getAllPigmyDeposits(depositApprovalDto.dateFrom, depositApprovalDto.dateTo);
        if (CollectionUtils.isEmpty(pigmyDeposits)) {
            throw new EntityNotFoundException("pigmyDeposits");
        }
        return new ResponseEntity<>(pigmyDeposits, HttpStatus.OK);
    }

    @PostMapping("/pigmy-deposit")
    public ResponseEntity<PigmyDeposit> createPigmyDeposit(@Valid @RequestBody final PigmyDeposit pigmyDeposit, Principal principal) {
        bodDateService.checkBOD();
        Ledger ledger = accountHeadService.getLedgerByName("Pigmy Deposit");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head and ledger not found");
        }

        AccountFormat accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.DEPOSIT, "PIGMY_DEPOSIT");
        if (accountFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate account number");
        }
        String accountNumber = accountFormat.getPrefix() + "-" + (accountFormat.getFromAccountNumber() + 1);

        NumberFormat numberFormat = memberService.getNumberFormatByType("Deposit_Receipt_Number");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate receipt number");
        }
        String receiptNumber = numberFormat.getPrefix() + "-" + (numberFormat.getReceiptNumber() + 1);

        Optional<Member> memberObj = memberService.getMember(pigmyDeposit.getMember().getId());
        if (!memberObj.isPresent()) {
            log.warn("Unable to find member with ID : {} not found", pigmyDeposit.getMember().getId());
            throw new EntityNotFoundException(Member.class.getName());
        }

        Optional<PigmyAgent> pigmyAgentObj = pigmyAgentService.getPigmyAgentById(pigmyDeposit.getPigmyAgent().getId());
        if (!pigmyAgentObj.isPresent()) {
            log.warn("Unable to find pigmyAgentObj with ID : {} not found", pigmyDeposit.getPigmyAgent().getId());
            throw new EntityNotFoundException(Member.class.getName());
        }
        Member member = memberObj.get();
        PigmyAgent pigmyAgent = pigmyAgentObj.get();
        pigmyDeposit.setMember(member);
        pigmyDeposit.setBalance(pigmyDeposit.getDepositAmount());
        pigmyDeposit.setPigmyAgent(pigmyAgent);

        savingsBankDepositService.checkSavingBankAccount(member);
        pigmyDeposit.setVoucherType("RECEIPT");
        pigmyDeposit.setCalculatedInterest(BigDecimal.ZERO);
        pigmyDeposit.setTransactionType("CREDIT");
        pigmyDeposit.setTransactionBy(getLoggedUser(principal));
        Optional<DepositAccountNumberMaster> accountNumberMaster = depositAccountNumberMasterService.getDepositAccountNumberMasterById(1);
//        pigmyDeposit.setAccountNumber(accountNumberMaster.get().getAccountNumberPG());
        pigmyDeposit.setApplicationNumber(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberPG()) + defaultApplicationNumber));
        accountNumberMaster.get().setAccountNumberPG(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberPG()) + 1));
        depositAccountNumberMasterService.saveDepositAccountNumberMaster(accountNumberMaster.get());
        pigmyDeposit.setAccountHead(ledger.getAccountHead());
        pigmyDeposit.setLedger(ledger);
        pigmyDeposit.setAccountNumber(accountNumber);
        pigmyDeposit.setReceiptNumber(receiptNumber);

        Date maturityYear = DateUtil.addYearsToDate(pigmyDeposit.getPeriodOfDeposit().intValue());
        pigmyDeposit.setMaturityDate(maturityYear);

        PigmyDeposit persistedPigmyDeposit = pigmyDepositService.createPigmyDeposit(pigmyDeposit);
        if (persistedPigmyDeposit != null) {
            PigmyDepositTransaction pigmyDepositTransaction = new PigmyDepositTransaction();
            pigmyDepositTransaction.setCreditAmount(persistedPigmyDeposit.getDepositAmount());
            pigmyDepositTransaction.setTransactionType("CREDIT");
            pigmyDepositTransaction.setBalance(persistedPigmyDeposit.getDepositAmount());
            pigmyDepositTransaction.setPigmyDeposit(persistedPigmyDeposit);
            pigmyDepositTransaction.setAccountNumber(persistedPigmyDeposit.getAccountNumber());
            pigmyDepositTransaction.setDebitAmount(new BigDecimal(0));
            User user = getLoggedUser(principal);
            pigmyDepositTransaction.setTransactionBy(user);
            pigmyDepositTransactionService.createPigmyDepositTransaction(pigmyDepositTransaction);
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "PIGMY ACCOUNT CREATED", "PIGMY ACCOUNT CREATED POST METHOD", loggedUser.getId());
            accountFormat.setFromAccountNumber(accountFormat.getFromAccountNumber() + 1);
            accountFormatService.saveAccountNumber(accountFormat);
        }
        return new ResponseEntity<>(persistedPigmyDeposit, HttpStatus.CREATED);
    }

    @PostMapping("/pigmy-deposit/approval")
    public ResponseEntity<PigmyDeposit> savingsPigmyDepositApproval(@RequestBody DepositApprovalDto depositApprovalDto, Principal principal) {
        DepositsApproval depositsApproval = saveDepositsApproval(principal);
        Optional<PigmyDeposit> persistedPigmyDeposit = null;
        for (long id : depositApprovalDto.ids) {
            persistedPigmyDeposit = pigmyDepositService.getPigmyDepositById(id);
            if (!persistedPigmyDeposit.isPresent()) {
                throw new EntityNotFoundException(PigmyDeposit.class.getSimpleName());
            }
            persistedPigmyDeposit.get().setDepositsApproval(depositsApproval);
            persistedPigmyDeposit.get().setStatus(true);
            PigmyDeposit savingsPigmyDeposit = pigmyDepositService.createPigmyDeposit(persistedPigmyDeposit.get());
            if (savingsPigmyDeposit != null) {
                transactionCreditEntry(savingsPigmyDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(savingsPigmyDeposit.getDepositAmount(), "CREDIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "PIGMY DEPOSIT ACCOUNT APPROVED", "PIGMY DEPOSIT POST METHOD", loggedUser.getId());
            }
        }
        return new ResponseEntity<>(persistedPigmyDeposit.get(), HttpStatus.OK);
    }

    public DepositsApproval saveDepositsApproval(Principal principal) {
        DepositsApproval depositsApproval = new DepositsApproval();
        depositsApproval.setApproved(true);
        depositsApproval.setApprovedBy(getLoggedUser(principal).getFirstName());
        depositsApprovalService.saveDepositsApproval(depositsApproval);
        return depositsApproval;
    }

    @PostMapping("/PIGMY_DEPOSIT/add-nominee/{id}")
    public ResponseEntity<PigmyDeposit> addNomineeDetails(@PathVariable long id, @Valid @RequestBody DepositNomineeDto depositNomineeDto) {
        Optional<PigmyDeposit> persistedDeposit = pigmyDepositService.getPigmyDepositById(id);
        PigmyDeposit pigmyDeposit = persistedDeposit.get();
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException(PigmyDeposit.class.getSimpleName());
        }
        pigmyDeposit.setDepositNomineeDetailsTwo(depositNomineeDto.getDepositNomineeDetailsTwo());
        pigmyDeposit.setDepositNomineeDetailsThree(depositNomineeDto.getDepositNomineeDetailsThree());
        PigmyDeposit savePigmyDeposit = pigmyDepositService.createPigmyDeposit(pigmyDeposit);
        return new ResponseEntity<>(savePigmyDeposit, HttpStatus.OK);
    }

    @PutMapping("/pigmy-deposit/{id}")
    public ResponseEntity<PigmyDeposit> updatePigmyDeposit(@PathVariable long id, @Valid @RequestBody PigmyDeposit pigmyDeposit, Principal principal) {
        Optional<PigmyDeposit> pigmyDepositById = pigmyDepositService.getPigmyDepositById(id);
        if (!pigmyDepositById.isPresent()) {
            throw new EntityNotFoundException(PigmyDeposit.class.getSimpleName());
        }
        pigmyDeposit.setId(id);
        pigmyDeposit.setCreatedOn(pigmyDepositById.get().getCreatedOn());
        PigmyDeposit savePigmyDeposit = pigmyDepositService.createPigmyDeposit(pigmyDeposit);
        if (savePigmyDeposit != null) {
            User loggedUser = getLoggedUser(principal);
            ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "FIXED DEPOSIT ACCOUNT Modified", "FIXED DEPOSIT ACCOUNT Modified PUT METHOD", loggedUser.getId());
        }
        return new ResponseEntity<>(savePigmyDeposit, HttpStatus.OK);
    }

    @PostMapping("/PIGMY_DEPOSIT/by-customer-id")
    public ResponseEntity<List<PigmyDeposit>> pigmyDepositByCustomerId(@RequestBody DepositReportDto depositReportDto) {
        List<PigmyDeposit> pigmyDeposits = pigmyDepositService.getPigmyDepositByCustomerId(depositReportDto.customerId);
        if (pigmyDeposits.isEmpty()) {
            throw new EntityNotFoundException(FixedDeposit.class.getSimpleName());
        }
        return new ResponseEntity<>(pigmyDeposits, HttpStatus.OK);
    }

    private void transactionCreditEntry(PigmyDeposit pigmyDeposit) {
        Ledger ledger = accountHeadService.getLedgerByName("Pigmy Deposit");
        if(ledger == null){
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Initial Amount Credited for PIGMY DEPOSIT Account " + pigmyDeposit.getAccountNumber() );
            creditTransaction.setCreditAmount(pigmyDeposit.getDepositAmount());
            creditTransaction.setTransactionBy(pigmyDeposit.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setParticulars("PIGMY DEPOSIT");
            creditTransaction.setTransferType(pigmyDeposit.getModeOfPayment());
            creditTransaction.setTransactionType(pigmyDeposit.getTransactionType());
            creditTransaction.setVoucherType(pigmyDeposit.getVoucherType());
            creditTransaction.setTransferType("Cash");
            creditTransaction.setAccountNumber(pigmyDeposit.getAccountNumber());
            creditTransaction.setAccountName(pigmyDeposit.getMember().getName() + " (" + pigmyDeposit.getMember().getMemberNumber() + ")");

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);
//            String accHeadName = "DEPOSITS";
//            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName, CREDIT);
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            Transaction persistedDebitTransaction = transactionService.transactionEntry(creditTransaction);

            TransactionUtil transactionUtil = new TransactionUtil(bankService);
            transactionUtil.getUpdateSocietyBalance(pigmyDeposit.getDepositAmount(), "CREDIT");
        }
    }

    @GetMapping("/member-pigmy-deposit-account/{memberId}")
    public ResponseEntity<List<PigmyDeposit>> getPigmyDepositAccounts(@PathVariable("memberId") long memberId) {

        Optional<Member> memberObj = memberService.getMember(memberId);
        if (!memberObj.isPresent()) {
            throw new EntityNotFoundException("No such a member exists");
        }

        Member member = memberObj.get();
        List<PigmyDeposit> PigmyDeposits = pigmyDepositService.getAllPigmyDepositByMember(member);
        if (CollectionUtils.isEmpty(PigmyDeposits)) {
            throw new EntityNotFoundException("No PigmyDeposits accounts for the provided member");
        }
        return new ResponseEntity<>(PigmyDeposits, HttpStatus.OK);
    }

    @GetMapping("/pigmy-deposit-by-account-number/{accountNumber}")
    public ResponseEntity<PigmyDeposit> getPigmyDepositByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
        Optional<PigmyDeposit> pigmyDepositOptional = pigmyDepositService.getPigmyDepositByAccountNumber(accountNumber);
        if (!pigmyDepositOptional.isPresent()) {
            throw new EntityNotFoundException("No pigmy deposit found for account " + accountNumber);
        }
        PigmyDeposit pigmyDeposit = pigmyDepositOptional.get();
        return new ResponseEntity<>(pigmyDeposit, HttpStatus.OK);
    }

    @PostMapping("pigmy-deposit/manual-entry")
    public ResponseEntity<PigmyDeposit> getPigmyDepositByAccountNumber(@RequestBody DepositReportDto depositReportDto,
                                                                       Principal principal) {
        Optional<PigmyDeposit> pigmyDepositOptional = pigmyDepositService.getPigmyDepositByAccountNumberByStatus(depositReportDto.getPigmyAccNumber());
        if (!pigmyDepositOptional.isPresent()) {
            throw new EntityNotFoundException("No Active Deposits found for the account number: " + depositReportDto.accountNumber);
        }

        PigmyDeposit pigmyDeposit = pigmyDepositOptional.get();
        if (pigmyDeposit != null) {

            BigDecimal balance = pigmyDeposit.getBalance();
            balance = balance.add(depositReportDto.getAmount());
            pigmyDeposit.setBalance(balance);

            BigDecimal depositAmount = depositReportDto.getAmount();
            // depositAmount = depositAmount.add(depositReportDto.getAmount());
            pigmyDeposit.setDepositAmount(depositAmount);

            PigmyDeposit persistedObject = pigmyDepositService.createPigmyDeposit(pigmyDeposit);
            if (persistedObject != null) {
                transactionCreditEntry(persistedObject);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(persistedObject.getDepositAmount(), "CREDIT");

                User loggedUser = getLoggedUser(principal);
                updatePigmyTransaction(persistedObject, loggedUser);
                String message = "Manually pigmy entry of amount : " + depositReportDto.getAmount() + " for account " + depositReportDto.getPigmyAccNumber() + " completed";
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), message, "PIGMY MANUAL ENTRY", loggedUser.getId());
            }
        }
        return new ResponseEntity(pigmyDeposit, HttpStatus.OK);
    }

    private void updatePigmyTransaction(PigmyDeposit persistedPigmyDeposit, User loggedUser) {
        PigmyDepositTransaction pigmyDepositTransaction = new PigmyDepositTransaction();
        pigmyDepositTransaction.setCreditAmount(persistedPigmyDeposit.getDepositAmount());
        pigmyDepositTransaction.setTransactionType("CREDIT");
        pigmyDepositTransaction.setBalance(persistedPigmyDeposit.getBalance());
        pigmyDepositTransaction.setPigmyDeposit(persistedPigmyDeposit);
        pigmyDepositTransaction.setAccountNumber(persistedPigmyDeposit.getAccountNumber());
        pigmyDepositTransaction.setDebitAmount(new BigDecimal(0));
        pigmyDepositTransaction.setTransactionBy(loggedUser);
        pigmyDepositTransactionService.createPigmyDepositTransaction(pigmyDepositTransaction);
    }

    @PostMapping("/PIGMY_DEPOSIT/refund")
    public ResponseEntity<PigmyDeposit> refundMatureFixedDeposit(@RequestBody DepositReportDto depositReportDto, Principal principal) throws ParseException {
        bodDateService.checkBOD();
        PigmyDeposit persistedDeposit;
        Optional<PigmyDeposit> pigmyDepositOptional = pigmyDepositService.getPigmyDepositByAccountNumberByStatus(depositReportDto.accountNumber);
        if (!pigmyDepositOptional.isPresent()) {
            throw new EntityNotFoundException("Please enter correct pigmy account no : " + depositReportDto.accountNumber);
        }
        PigmyDeposit pigmyDeposit = pigmyDepositOptional.get();

        pigmyDepositService.checkPigmyDepositForLoan(pigmyDeposit.getAccountNumber());

        PigmyDepositDto pigmyDepositDto = null;

        if (pigmyDeposit.getMaturityDate().after(DateUtil.getTodayDate())) {
            // Pigmy interest calculation
            pigmyDepositDto = pigmyInterestCalulationForPreMaturity("" + pigmyDeposit.getAccountNumber());
        } else {
            pigmyDepositDto = pigmyInterestCalulationForMaturity("" + pigmyDeposit.getAccountNumber());
        }
        pigmyDeposit.setWithDrawn(true);
        pigmyDeposit.setWithdrawnOn(DateUtil.getTodayDate());
        pigmyDeposit.setModeOfPayment(depositReportDto.getModeOfPayment());
        pigmyDeposit.setAccountCloserDate(DateUtil.getTodayDate());
        pigmyDeposit.setCalculatedInterest(pigmyDepositDto.getInterestAmount());
        pigmyDeposit.setMaturityDate(pigmyDepositDto.getMaturityDate());
        pigmyDeposit.setMaturityAmount(pigmyDepositDto.getMaturityAmount());
        pigmyDeposit.setPeriodOfDeposit(pigmyDepositDto.getPeriodOfDeposit());

        pigmyDeposit.setWithdrawAmount(pigmyDeposit.getMaturityAmount());

        BigDecimal balance = pigmyDeposit.getBalance().subtract(pigmyDeposit.getWithdrawAmount());
        pigmyDeposit.setBalance(balance);

        persistedDeposit = pigmyDepositService.createPigmyDeposit(pigmyDeposit);

        transactionDebitEntry(persistedDeposit);
        TransactionUtil transactionUtil = new TransactionUtil(bankService);
        transactionUtil.getUpdateSocietyBalance(persistedDeposit.getMaturityAmount(), "DEBIT");


        if (depositReportDto.getModeOfPayment().equalsIgnoreCase("Transfer")) {
            User loggedUser = getLoggedUser(principal);
            depositRefundCreditTransactionEntry(persistedDeposit, loggedUser);
        }
        return new ResponseEntity<>(persistedDeposit, HttpStatus.OK);
    }

    private void depositRefundCreditTransactionEntry(PigmyDeposit pigmyDeposit, User user) {

        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(pigmyDeposit.getMember().getMemberNumber());

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
            depositBalance = depositBalance.add(pigmyDeposit.getMaturityAmount());

            SavingBankTransaction sbTransaction = new SavingBankTransaction();

            sbTransaction.setTransactionType("CREDIT");
            sbTransaction.setCreditAmount(pigmyDeposit.getMaturityAmount());

            sbTransaction.setBalance(depositBalance);
            sbTransaction.setDebitAmount(BigDecimal.ZERO);
            sbTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
            sbTransaction.setTransactionBy(user);
            sbTransaction.setSavingsBankDeposit(savingsBankDeposit);

            savingBankTransactionService.createSavingBankTransaction(sbTransaction);

            savingsBankDeposit.setBalance(depositBalance);

            savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

            if (savingBankTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credited to saving bank Acc No. i.e Pigmy refund" + savingBankTransaction.getAccountNumber() + " submitted",
                        "SAVING BANK TRANSACTION", user.getId());
            }
            TransactionUtil transactionUtil = new TransactionUtil(bankService);
            transactionUtil.getUpdateSocietyBalance(pigmyDeposit.getMaturityAmount(), "CREDIT");
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            Transaction creditTransaction = new Transaction();

            creditTransaction.setRemark("Amount credited to Saving Bank Acc No. ie Pigmy refund: " + savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setCreditAmount(pigmyDeposit.getMaturityAmount());
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransferType(pigmyDeposit.getModeOfPayment());
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountName(pigmyDeposit.getMember().getName() + " (" + pigmyDeposit.getMember().getMemberNumber() + ")");


            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(pigmyDeposit.getMaturityAmount());
            creditTransaction.setBalance(balance);

            transactionService.transactionEntry(creditTransaction);

            if (creditTransaction != null) {
                String message = "" + creditTransaction.getCreditAmount() + " Amount credited to saving bank Acc No. i.e pigmy refund: to" + savingsBankDeposit.getAccountNumber() + " for : " + pigmyDeposit.getMember().getName();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Pigmy Deposit Module", user.getId());
            }
            if (creditTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credit from society share bank Acc No. " + creditTransaction.getId() + " submitted",
                        "Pigmy Deposit Refund", user.getId());
            }
        }
    }

    // In case of SB Credit refund
    private void transactionDebitEntry(PigmyDeposit pigmyDeposit) {
        Ledger ledger = accountHeadService.getLedgerByName("Pigmy Deposit");
        if(ledger == null){
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(pigmyDeposit.getMaturityAmount());
            debitTransaction.setRemark("Amount debited for pigmy deposit withdrawn " + pigmyDeposit.getAccountNumber());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(pigmyDeposit.getTransactionBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setParticulars("PIGMY DEPOSIT");
            debitTransaction.setVoucherType(pigmyDeposit.getVoucherType());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);
            debitTransaction.setAccountName(pigmyDeposit.getMember().getName() + " (" + pigmyDeposit.getMember().getMemberNumber() + ")");


            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountNumber(pigmyDeposit.getAccountNumber());
            debitTransaction.setTransferType(pigmyDeposit.getModeOfPayment());
            transactionService.transactionEntry(debitTransaction);
        }
    }

    public PigmyDepositDto pigmyInterestCalulationForMaturity(@PathVariable String accountNumber) throws ParseException {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        List<PigmyDepositTransaction> pigmyCollectionByYear = new ArrayList<>();
        List<PigmyDepositTransaction> pigmyCollectionByMonth = new ArrayList<>();
        BigDecimal principal = BigDecimal.ZERO;
        double interestAmount = 0.0;
        List<PigmyDepositTransaction> pigmyDepositTransaction = pigmyDepositTransactionService.getAllPigmyDepositTransactions(accountNumber);
        Optional<PigmyDeposit> pigmyDepositObj = pigmyDepositService.getPigmyDepositByAccountNumberByStatus(accountNumber);
        PigmyDeposit pigmyDeposit = pigmyDepositObj.get();
        if (!(pigmyDepositTransaction != null && pigmyDepositTransaction.size() > 0)) {
            throw new EntityNotFoundException("No pigmy deposit transaction for account: " + accountNumber);
        }
        for (int month = 1; month <= 12; month++) {
            for (PigmyDepositTransaction depositTransaction : pigmyDepositTransaction) {
                String monthName = monthFormat.format(depositTransaction.getCreatedOn());
                if (month == Integer.parseInt(monthName)) {
                    pigmyCollectionByMonth.add(depositTransaction);
                }
            }
            if (pigmyCollectionByMonth.size() > 4) {
                for (PigmyDepositTransaction pigmyMonthwise : pigmyCollectionByMonth) {
                    pigmyCollectionByYear.add(pigmyMonthwise);
                }
            }
            pigmyCollectionByMonth.clear();
        }
        for (PigmyDepositTransaction pigmyDepositTransactionYear : pigmyCollectionByYear) {
            principal = principal.add(pigmyDepositTransactionYear.getCreditAmount());
        }
        PigmyDepositDto pigmyDepositDto = new PigmyDepositDto();
        // if (pigmyDeposit.getMaturityDate().before(DateUtil.getTodayDate()) || (pigmyDeposit.getMaturityDate().equals(DateUtil.getTodayDate()))) {
        interestAmount = pigmyInterestCalculation(principal.doubleValue(), 5, pigmyDeposit.getPeriodOfDeposit().doubleValue());
        pigmyDepositDto.setMaturityDate(DateUtil.getTodayDate());
        int periodOfDeposit = DateUtil.calculateYearsBetweenDate(pigmyDeposit.getCreatedOn(), DateUtil.getTodayDate());
        pigmyDepositDto.setPeriodOfDeposit(BigDecimal.valueOf(periodOfDeposit));
        /*} else {
            throw new EntityNotFoundException("Maturity date is :" + pigmyDeposit.getMaturityDate());
        }*/
        pigmyDepositDto.setAccountNumber(accountNumber);
        pigmyDepositDto.setInterestAmount(BigDecimal.valueOf(interestAmount));
        pigmyDepositDto.setPrincipalAmount(principal);
        BigDecimal maturityAmount = principal.add(BigDecimal.valueOf(interestAmount));
        pigmyDepositDto.setMaturityAmount(maturityAmount);
        pigmyDepositDto.setMaturityDate(DateUtil.getTodayDate());
        pigmyDepositDto.setPeriodOfDeposit(pigmyDepositDto.getPeriodOfDeposit());
        pigmyDepositDto.setAccountOpenDate(pigmyDeposit.getCreatedOn());
        return pigmyDepositDto;
    }

    @PostMapping("/PIGMY_DEPOSIT/refund/pre-mature")
    public ResponseEntity<PigmyDeposit> refundPreMatureFixedDeposit(@RequestBody DepositReportDto depositReportDto, Principal principal) throws ParseException {
        bodDateService.checkBOD();
        PigmyDeposit persistedDeposit;
        Optional<PigmyDeposit> pigmyDepositOptional = pigmyDepositService.getPigmyDepositByAccountNumberByStatus(depositReportDto.accountNumber);
        if (!pigmyDepositOptional.isPresent()) {
            throw new EntityNotFoundException("Please enter correct pigmy account no : " + depositReportDto.accountNumber);
        }
        PigmyDeposit pigmyDeposit = pigmyDepositOptional.get();

        if (pigmyDeposit.getMaturityDate().after(DateUtil.getTodayDate())) {
            // Pigmy interest calculation
            PigmyDepositDto pigmyDepositDto = pigmyInterestCalulationForPreMaturity("" + pigmyDeposit.getAccountNumber());
            pigmyDeposit.setWithDrawn(true);
            pigmyDeposit.setWithdrawnOn(DateUtil.getTodayDate());
            pigmyDeposit.setModeOfPayment(depositReportDto.getModeOfPayment());
            pigmyDeposit.setAccountCloserDate(DateUtil.getTodayDate());
            pigmyDeposit.setCalculatedInterest(pigmyDepositDto.getInterestAmount());
            pigmyDeposit.setMaturityDate(pigmyDepositDto.getMaturityDate());
            pigmyDeposit.setMaturityAmount(pigmyDepositDto.getMaturityAmount());
            pigmyDeposit.setPeriodOfDeposit(pigmyDepositDto.getPeriodOfDeposit());

            pigmyDeposit.setWithdrawAmount(pigmyDeposit.getMaturityAmount());

            BigDecimal balance = pigmyDeposit.getBalance().subtract(pigmyDeposit.getWithdrawAmount());
            pigmyDeposit.setBalance(balance);

            persistedDeposit = pigmyDepositService.createPigmyDeposit(pigmyDeposit);

            transactionDebitEntry(persistedDeposit);
            TransactionUtil transactionUtil = new TransactionUtil(bankService);
            transactionUtil.getUpdateSocietyBalance(persistedDeposit.getMaturityAmount(), "DEBIT");


            if (depositReportDto.getModeOfPayment().equalsIgnoreCase("Transfer")) {
                User loggedUser = getLoggedUser(principal);
                depositRefundCreditTransactionEntry(persistedDeposit, loggedUser);
            }

        } else {
            throw new EntityNotFoundException("Pigmy deposit account maturity not yet completed");
        }

        return new ResponseEntity<>(persistedDeposit, HttpStatus.OK);
    }

    public PigmyDepositDto pigmyInterestCalulationForPreMaturity(@PathVariable String accountNumber) throws ParseException {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        List<PigmyDepositTransaction> pigmyCollectionByYear = new ArrayList<>();
        List<PigmyDepositTransaction> pigmyCollectionByMonth = new ArrayList<>();
        BigDecimal principal = BigDecimal.ZERO;
        double interestAmount = 0.0;
        double periodOfDeposit = 0.0;
        List<PigmyDepositTransaction> pigmyDepositTransaction = pigmyDepositTransactionService.getAllPigmyDepositTransactions(accountNumber);
        Optional<PigmyDeposit> pigmyDepositObj = pigmyDepositService.getPigmyDepositByAccountNumberByStatus(accountNumber);
        PigmyDeposit pigmyDeposit = pigmyDepositObj.get();
        if (!(pigmyDepositTransaction != null && pigmyDepositTransaction.size() > 0)) {
            throw new EntityNotFoundException("No pigmy deposit transaction for account: " + accountNumber);
        }
        for (int month = 1; month <= 12; month++) {
            for (PigmyDepositTransaction depositTransaction : pigmyDepositTransaction) {
                String monthName = monthFormat.format(depositTransaction.getCreatedOn());
                if (month == Integer.parseInt(monthName)) {
                    pigmyCollectionByMonth.add(depositTransaction);
                }
            }
            if (pigmyCollectionByMonth.size() > 4) {
                for (PigmyDepositTransaction pigmyMonthwise : pigmyCollectionByMonth) {
                    pigmyCollectionByYear.add(pigmyMonthwise);
                }
            }
            pigmyCollectionByMonth.clear();
        }
        for (PigmyDepositTransaction pigmyDepositTransactionYear : pigmyCollectionByYear) {
            principal = principal.add(pigmyDepositTransactionYear.getCreditAmount());
        }
        PigmyDepositDto pigmyDepositDto = new PigmyDepositDto();
        if (pigmyDeposit.getMaturityDate().before(DateUtil.getTodayDate()) || (pigmyDeposit.getMaturityDate().equals(DateUtil.getTodayDate()))) {
            interestAmount = pigmyInterestCalculation(principal.doubleValue(), 5, pigmyDeposit.getPeriodOfDeposit().doubleValue());
            pigmyDepositDto.setMaturityDate(pigmyDeposit.getMaturityDate());
            pigmyDepositDto.setPeriodOfDeposit(pigmyDeposit.getPeriodOfDeposit());
        } else {
            periodOfDeposit = DateUtil.calculateDaysBetweenDate(pigmyDeposit.getCreatedOn(), DateUtil.getTodayDate());
            periodOfDeposit = periodOfDeposit / 365;
            interestAmount = pigmyInterestCalculation(principal.doubleValue(), 3, periodOfDeposit);
        }
        pigmyDepositDto.setAccountNumber(accountNumber);
        pigmyDepositDto.setInterestAmount(BigDecimal.valueOf(interestAmount));
        pigmyDepositDto.setPrincipalAmount(principal);
        BigDecimal maturityAmount = principal.add(BigDecimal.valueOf(interestAmount));
        pigmyDepositDto.setMaturityAmount(maturityAmount);
        pigmyDepositDto.setMaturityDate(DateUtil.getTodayDate());
        pigmyDepositDto.setPeriodOfDeposit(BigDecimal.valueOf(periodOfDeposit));
        pigmyDepositDto.setAccountOpenDate(pigmyDeposit.getCreatedOn());
        return pigmyDepositDto;
    }

    @PostMapping("/PIGMY_DEPOSIT/refund/premature/view")
    public ResponseEntity<PigmyDeposit> refundPigmyDepositPreMatureView(@RequestBody DepositReportDto depositReportDto, Principal principal) throws ParseException {
        bodDateService.checkBOD();
        PigmyDeposit persistedDeposit;
        Optional<PigmyDeposit> pigmyDepositOptional = pigmyDepositService.getPigmyDepositByAccountNumberByStatus(depositReportDto.accountNumber);
        if (!pigmyDepositOptional.isPresent()) {
            throw new EntityNotFoundException("Please enter correct pigmy account no : " + depositReportDto.accountNumber);
        }
        PigmyDeposit pigmyDeposit = pigmyDepositOptional.get();

        // Pigmy interest calculation
        PigmyDepositDto pigmyDepositDto = pigmyInterestCalulationForPreMaturity("" + pigmyDeposit.getAccountNumber());

        return new ResponseEntity(pigmyDepositDto, HttpStatus.OK);
    }

    @PostMapping("/PIGMY_DEPOSIT/refund/mature/view")
    public ResponseEntity<PigmyDeposit> refundPigmyDepositMatureView(@RequestBody DepositReportDto depositReportDto, Principal principal) throws ParseException {
        bodDateService.checkBOD();
        PigmyDeposit persistedDeposit;
        Optional<PigmyDeposit> pigmyDepositOptional = pigmyDepositService.getPigmyDepositByAccountNumberByStatus(depositReportDto.accountNumber);
        if (!pigmyDepositOptional.isPresent()) {
            throw new EntityNotFoundException("Please enter correct pigmy account no : " + depositReportDto.accountNumber);
        }
        PigmyDeposit pigmyDeposit = pigmyDepositOptional.get();

        // Pigmy interest calculation
        PigmyDepositDto pigmyDepositDto = pigmyInterestCalulationForMaturity("" + pigmyDeposit.getAccountNumber());

        return new ResponseEntity(pigmyDepositDto, HttpStatus.OK);
    }
}