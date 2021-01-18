package com.dfq.coeffi.cbs.deposit.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLog;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.Dto.DepositApprovalDto;
import com.dfq.coeffi.cbs.deposit.Dto.DepositNomineeDto;
import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransactionService;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.deposit.service.CurrentAccountService;
import com.dfq.coeffi.cbs.deposit.service.DepositAccountNumberMasterService;
import com.dfq.coeffi.cbs.deposit.service.DepositsApprovalService;
import com.dfq.coeffi.cbs.deposit.service.SavingsBankDepositService;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.CREDIT;

@Slf4j
@RestController
public class CurrentAccountApi extends BaseController {

    private final CurrentAccountService currentAccountService;
    private final DepositsApprovalService depositsApprovalService;
    private final MemberService memberService;
    private final ApplicationLogService applicationLogService;
    public final BankService bankService;
    public final AccountFormatService accountFormatService;
    private final SavingBankTransactionService savingBankTransactionService;

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
    private CurrentAccountTransactionService currentAccountTransactionService;

    @Autowired
    private CurrentAccountApi(final CurrentAccountService currentAccountService, DepositsApprovalService depositsApprovalService,
                              MemberService memberService, ApplicationLogService applicationLogService,
                              final BankService bankService, final AccountFormatService accountFormatService, SavingBankTransactionService savingBankTransactionService) {
        this.currentAccountService = currentAccountService;
        this.depositsApprovalService = depositsApprovalService;
        this.memberService = memberService;
        this.applicationLogService = applicationLogService;
        this.bankService = bankService;
        this.accountFormatService = accountFormatService;
        this.savingBankTransactionService = savingBankTransactionService;
    }

    @PostMapping("/current-account/unapprove-list")
    public ResponseEntity<List<CurrentAccount>> getAllCurrentAccountDeposits(@RequestBody DepositApprovalDto depositApprovalDto) {
        List<CurrentAccount> allCurrentAccountDeposits = currentAccountService.getAllCurrentAccountDeposits(depositApprovalDto.dateFrom, depositApprovalDto.dateTo);
        if (CollectionUtils.isEmpty(allCurrentAccountDeposits)) {
            throw new EntityNotFoundException("allCurrentAccountDeposits");
        }
        return new ResponseEntity<>(allCurrentAccountDeposits, HttpStatus.OK);
    }

    @PostMapping("/current-account")
    public ResponseEntity<CurrentAccount> createCurrentAccount(@Valid @RequestBody final CurrentAccount currentAccount, Principal principal) {
        bodDateService.checkBOD();
        CurrentAccount persistedCurrentAccount = null;

        Ledger ledger = accountHeadService.getLedgerByName("Current Account");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head and ledger not found");
        }

        AccountFormat accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.DEPOSIT, "CURRENT_ACCOUNT");
        if (accountFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate account number");
        }
        String accountNumber = accountFormat.getPrefix() + "-" + (accountFormat.getFromAccountNumber() + 1);

        NumberFormat numberFormat = memberService.getNumberFormatByType("Deposit_Receipt_Number");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate receipt number");
        }
        String receiptNumber = numberFormat.getPrefix() + "-" + (numberFormat.getReceiptNumber() + 1);

        Optional<Member> memberObj = memberService.getMember(currentAccount.getMember().getId());
        if (!memberObj.isPresent()) {
            log.warn("Unable to find member with ID : {} not found", currentAccount.getMember().getId());
            throw new EntityNotFoundException(Member.class.getName());
        }

        Member member = memberObj.get();

        if (member.getMemberType() == MemberType.NOMINAL) {
            throw new EntityNotFoundException("Selected Member is nominal member, can not open Current Account");
        }
        currentAccount.setMember(member);
        Optional<DepositAccountNumberMaster> accountNumberMaster = depositAccountNumberMasterService.getDepositAccountNumberMasterById(1);
        currentAccount.setApplicationNumber(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberCA()) + defaultApplicationNumber));
        accountNumberMaster.get().setAccountNumberCA(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberCA()) + 1));
        currentAccount.setTransactionBy(getLoggedUser(principal));
        currentAccount.setTransactionType("CREDIT");
        currentAccount.setVoucherType("RECEIPT");
        currentAccount.setBalance(currentAccount.getDepositAmount());
        currentAccount.setAccountHead(ledger.getAccountHead());
        currentAccount.setLedger(ledger);
        currentAccount.setAccountNumber(accountNumber);
        currentAccount.setReceiptNumber(receiptNumber);

        if (member.getOrganisationName().equalsIgnoreCase("Individual")) {
            throw new EntityNotFoundException("Current account can not be created for the selected member");
        } else {
            persistedCurrentAccount = currentAccountService.saveCurrentAccount(currentAccount);
        }

        if (persistedCurrentAccount != null) {
            CurrentAccountTransaction caTransaction = new CurrentAccountTransaction();
            caTransaction.setCreditAmount(persistedCurrentAccount.getDepositAmount());
            caTransaction.setTransactionType("CREDIT");
            caTransaction.setBalance(persistedCurrentAccount.getDepositAmount());
            caTransaction.setCurrentAccount(persistedCurrentAccount);
            caTransaction.setAccountNumber(persistedCurrentAccount.getAccountNumber());
            User user = getLoggedUser(principal);
            caTransaction.setTransactionBy(user);
            currentAccountTransactionService.createCurrentAccountTransaction(caTransaction);
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "CURRENT ACCOUNT CREATED", "CURRENT ACCOUNT:POST OPERATION", loggedUser.getId());
            accountFormat.setFromAccountNumber(accountFormat.getFromAccountNumber() + 1);
            accountFormatService.saveAccountNumber(accountFormat);

            numberFormat.setReceiptNumber(numberFormat.getReceiptNumber() + 1);
            memberService.updateNumberFormat(numberFormat);

            SavingsBankDeposit savingsBankDeposit = new SavingsBankDeposit();
            savingsBankDeposit.setMember(persistedCurrentAccount.getMember());
            savingsBankDeposit.setApplicationNumber(persistedCurrentAccount.getApplicationNumber());
            savingsBankDeposit.setTransactionBy(getLoggedUser(principal));
            savingsBankDeposit.setBalance(persistedCurrentAccount.getDepositAmount());
            savingsBankDeposit.setAccountHead(ledger.getAccountHead());
            savingsBankDeposit.setLedger(ledger);
            savingsBankDeposit.setAccountNumber(persistedCurrentAccount.getAccountNumber());
            savingsBankDeposit.setReceiptNumber(persistedCurrentAccount.getReceiptNumber());
            savingsBankDeposit.setDepositAmount(persistedCurrentAccount.getDepositAmount());
            savingsBankDeposit.setDepositType(DepositType.SAVING_BANK_DEPOSIT);
            savingsBankDeposit.setAccountType(AccountType.CURRENT);


            SavingsBankDeposit savingsBank = savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

            if (savingsBank != null) {
                SavingBankTransaction savingBankTransaction = new SavingBankTransaction();
                savingBankTransaction.setCreditAmount(savingsBankDeposit.getDepositAmount());
                savingBankTransaction.setTransactionType("CREDIT");
                savingBankTransaction.setBalance(savingsBankDeposit.getDepositAmount());
                savingBankTransaction.setSavingsBankDeposit(savingsBank);
                savingBankTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
                savingBankTransaction.setTransactionBy(user);
                savingBankTransactionService.createSavingBankTransaction(savingBankTransaction);
            }
        }
        return new ResponseEntity<>(persistedCurrentAccount, HttpStatus.CREATED);
    }

    @PutMapping("/current-account/{id}")
    public ResponseEntity<CurrentAccount> updateCurrentAccount(@PathVariable long id, @Valid @RequestBody CurrentAccount currentAccount, Principal principal) {
        Optional<CurrentAccount> persistedCurrentAccount = currentAccountService.getCurrentAccountDepositById(id);
        if (!persistedCurrentAccount.isPresent()) {
            throw new EntityNotFoundException(CurrentAccount.class.getSimpleName());
        }
        currentAccount.setId(id);
        currentAccount.setCreatedOn(persistedCurrentAccount.get().getCreatedOn());
        CurrentAccount saveCurrentAccount = currentAccountService.saveCurrentAccount(currentAccount);
        if (saveCurrentAccount != null) {
            User loggedUser = getLoggedUser(principal);
            ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "CURRENT ACCOUNT Modified", "CURRENT ACCOUNT:PUT OPERATION", loggedUser.getId());
        }
        return new ResponseEntity<>(currentAccount, HttpStatus.OK);
    }

    @PostMapping("CURRENT_ACCOUNT/add-nominee/{id}")
    public ResponseEntity<CurrentAccount> addNomineeDetails(@PathVariable long id, @Valid @RequestBody DepositNomineeDto depositNomineeDto) {
        Optional<CurrentAccount> persistedDeposit = currentAccountService.getCurrentAccountDepositById(id);
        CurrentAccount currentAccount = persistedDeposit.get();
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException(CurrentAccount.class.getSimpleName());
        }
        if (currentAccount.getDepositNomineeDetailsTwo() == null) {
            currentAccount.setDepositNomineeDetailsTwo(depositNomineeDto.getDepositNomineeDetailsTwo());
        } else if (currentAccount.getDepositNomineeDetailsThree() == null) {
            currentAccount.setDepositNomineeDetailsThree(depositNomineeDto.getDepositNomineeDetailsThree());
        }
        CurrentAccount childObj = currentAccountService.saveCurrentAccount(currentAccount);
        return new ResponseEntity<>(childObj, HttpStatus.OK);
    }

    @DeleteMapping("/current-account/{id}")
    public ResponseEntity<CurrentAccount> deleteCurrentAccount(@PathVariable Long id) {
        Optional<CurrentAccount> currentAccount = currentAccountService.getCurrentAccountDepositById(id);
        if (!currentAccount.isPresent()) {
            throw new EntityNotFoundException(CurrentAccount.class.getName());
        }
        currentAccountService.deleteCurrentAccountDeposit(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/current-account/approval")
    public ResponseEntity<CurrentAccount> currentAccountApproval(@RequestBody DepositApprovalDto depositApprovalDto, Principal principal) {
        DepositsApproval depositsApproval = saveDepositsApproval(principal);
        Optional<CurrentAccount> persistedCurrentAccount = null;
        for (long id : depositApprovalDto.ids) {
            persistedCurrentAccount = currentAccountService.getCurrentAccountDepositById(id);
            if (!persistedCurrentAccount.isPresent()) {
                throw new EntityNotFoundException(CurrentAccount.class.getSimpleName());
            }
            persistedCurrentAccount.get().setDepositsApproval(depositsApproval);
            persistedCurrentAccount.get().setStatus(true);
            persistedCurrentAccount.get().setAccountStatus("ACTIVE");
            CurrentAccount saveCurrentAccount = currentAccountService.saveCurrentAccount(persistedCurrentAccount.get());
            if (saveCurrentAccount != null) {

                transactionCreditEntry(saveCurrentAccount);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(saveCurrentAccount.getDepositAmount(), "CREDIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "CURRENT ACCOUNT APPROVED", "CURRENT ACCOUNT APPROVAL OPERATION", loggedUser.getId());
            }
        }
        return new ResponseEntity<>(persistedCurrentAccount.get(), HttpStatus.OK);
    }

    public DepositsApproval saveDepositsApproval(Principal principal) {
        DepositsApproval depositsApproval = new DepositsApproval();
        depositsApproval.setApproved(true);
        depositsApproval.setApprovedBy(getLoggedUser(principal).getFirstName());
        depositsApprovalService.saveDepositsApproval(depositsApproval);
        return depositsApproval;
    }

    private void transactionCreditEntry(CurrentAccount currentAccount) {
        Ledger ledger = accountHeadService.getLedgerByName("Current Account");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Initial Amount Credited for currentAccount Account " + currentAccount.getId());
            creditTransaction.setCreditAmount(currentAccount.getDepositAmount());
            creditTransaction.setTransactionBy(currentAccount.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setParticulars("CURRENT ACCOUNT DEPOSIT");
            creditTransaction.setTransactionType(currentAccount.getTransactionType());
            creditTransaction.setTransferType("Cash");
            creditTransaction.setVoucherType(currentAccount.getVoucherType());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName, CREDIT);
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountNumber(currentAccount.getAccountNumber());
            creditTransaction.setAccountName(currentAccount.getMember().getName() + " (" + currentAccount.getMember().getMemberNumber() + ")");

            Transaction persistedDebitTransaction = transactionService.transactionEntry(creditTransaction);
        }
    }

    @GetMapping("/member-current-account/{memberId}")
    public ResponseEntity<List<CurrentAccount>> getMemberCurrentAccounts(@PathVariable("memberId") long memberId) {

        Optional<Member> memberObj = memberService.getMember(memberId);
        if (!memberObj.isPresent()) {
            throw new EntityNotFoundException("No such a member exists");
        }

        Member member = memberObj.get();
        List<CurrentAccount> currentAccounts = currentAccountService.getAllCurrentAccountsByMember(member);
        if (CollectionUtils.isEmpty(currentAccounts)) {
            throw new EntityNotFoundException("No current accounts for the provided member");
        }
        return new ResponseEntity<>(currentAccounts, HttpStatus.OK);
    }

    @PostMapping("/current-account/deactivate")
    public ResponseEntity<CurrentAccount> deleteCurrentAccount(@RequestBody DepositApprovalDto depositApprovalDto, Principal principal) {
        Optional<CurrentAccount> currentAccountObj = currentAccountService.getCurrentAccountDepositByAccountNumber(depositApprovalDto.getAccountNumber());
        if (!currentAccountObj.isPresent()) {
            throw new EntityNotFoundException("Account not found for the acc no. :" + depositApprovalDto.getAccountNumber());
        }
        CurrentAccount currentAccount = currentAccountObj.get();
        Member member = currentAccount.getMember();
        savingsBankDepositService.checkLoanForMember(member);
        savingsBankDepositService.checkDepositForMember(member);
        if (currentAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new EntityNotFoundException("Current Account can not be closed !! Balance of Current Account is greater than 0 : Rs." + currentAccount.getBalance());
        }
        currentAccount.setAccountStatus("CLOSED");
        currentAccount.setAccountClosedOn(new Date());
        CurrentAccount persistedCurrentAccount = currentAccountService.saveCurrentAccount(currentAccount);
        if (persistedCurrentAccount != null) {
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Current Account De-activated Account number" + persistedCurrentAccount.getAccountNumber(), "CURRENT ACCOUNT POSTED", loggedUser.getId());
        }
        return new ResponseEntity<>(currentAccount, HttpStatus.OK);
    }
}