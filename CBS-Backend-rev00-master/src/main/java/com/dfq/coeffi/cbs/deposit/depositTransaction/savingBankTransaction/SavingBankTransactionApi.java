package com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction;

import com.dfq.coeffi.cbs.deposit.Dto.SavingBankTransactionDto;
import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransactionService;
import com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction.PigmyDepositTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction.PigmyDepositTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.deposit.service.*;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.service.LoanService;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.CREDIT;
import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.DEBIT;

@Transactional
@RestController
@Slf4j
public class SavingBankTransactionApi extends BaseController {

    @Autowired
    private SavingBankTransactionService savingBankTransactionService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountHeadService accountHeadService;

    @Autowired
    private SavingsBankDepositService savingsBankDepositService;

    @Autowired
    private CurrentAccountTransactionService currentAccountTransactionService;

    @Autowired
    private ChildrensDepositService childrensDepositService;

    @Autowired
    private DoubleSchemeService doubleSchemeService;
    @Autowired
    private FixedDepositService fixedDepositService;

    @Autowired
    private TermDepositService termDepositService;

    @Autowired
    private PigmyDepositTransactionService pigmyDepositTransactionService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private RecurringDepositService recurringDepositService;

    @Autowired
    private MemberService memberService;

    @PostMapping("saving-bank-deposit-transaction")
    public ResponseEntity<SavingBankTransaction> createSavingBankTransaction(@Valid @RequestBody final SavingBankTransaction savingBankTransaction, Principal principal) {
        SavingBankTransaction persistedDeposit = null;
        SavingBankTransaction latestRecord = savingBankTransactionService.getLatestTransactionOfSB(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
        BigDecimal latestBalanceAmount = latestRecord.getBalance();
        BigDecimal creditAmount = savingBankTransaction.getCreditAmount();
        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankDepositByAccountNumber(savingBankTransaction.getAccountNumber());
        if (savingBankTransaction.getTransactionType().equalsIgnoreCase("CREDIT")) {
            savingBankTransaction.setBalance(latestBalanceAmount.add(creditAmount));
            savingBankTransaction.setVoucherType("RECEIPT");
            savingBankTransaction.setDebitAmount(BigDecimal.ZERO);
            savingBankTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
            User loggedUser = getLoggedUser(principal);
            savingBankTransaction.setTransactionBy(loggedUser);
            persistedDeposit = savingBankTransactionService.createSavingBankTransaction(savingBankTransaction);
            savingsBankDeposit.setBalance(savingBankTransaction.getBalance());
            savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);
            if (persistedDeposit != null) {
                transactionCreditEntry(savingBankTransaction);
            }
        } else if ((savingBankTransaction.getTransactionType().equalsIgnoreCase("DEBIT") && (savingBankTransaction.getDebitAmount().compareTo(latestRecord.getBalance()) <= 0))) {
            savingBankTransaction.setBalance(latestBalanceAmount.subtract(savingBankTransaction.getDebitAmount()));
            savingBankTransaction.setVoucherType("PAYMENT");
            savingBankTransaction.setCreditAmount(BigDecimal.ZERO);
            savingBankTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
            persistedDeposit = savingBankTransactionService.createSavingBankTransaction(savingBankTransaction);
            if (persistedDeposit != null) {
                transactionDebitEntry(savingBankTransaction);
            }
        } else {
            log.warn("Withdraw amount is morethan balance amount");
        }
        return new ResponseEntity<>(persistedDeposit, HttpStatus.CREATED);
    }

    @PostMapping("saving-bank-deposit-transaction/latest")
    public ResponseEntity<SavingBankTransaction> getTransactionByAccountNumber(@Valid @RequestBody SavingBankTransactionDto savingBankTransactionDto) {

        if (savingBankTransactionDto.getDepositType().equalsIgnoreCase("SAVING_BANK")) {
            SavingBankTransaction latestTransactionOfSB = savingBankTransactionService.getLatestTransactionOfSB(savingBankTransactionDto.getAccountNumber());
            savingBankTransactionDto.setAccountNumber(latestTransactionOfSB.getAccountNumber());
            savingBankTransactionDto.setMember(latestTransactionOfSB.getSavingsBankDeposit().getMember());
            savingBankTransactionDto.setName(latestTransactionOfSB.getSavingsBankDeposit().getMember().getName());
            savingBankTransactionDto.setBalance(latestTransactionOfSB.getBalance());
            if (latestTransactionOfSB == null) {
                throw new EntityNotFoundException("latestTransactionOfSB is not found or Not approved");
            }
            return new ResponseEntity(savingBankTransactionDto, HttpStatus.OK);
        } else if (savingBankTransactionDto.getDepositType().equalsIgnoreCase("CURRENT_ACCOUNT")) {
            CurrentAccountTransaction currentAccountTransaction = currentAccountTransactionService.getLatestTransactionOfCurrentAccount(savingBankTransactionDto.getAccountNumber());
            savingBankTransactionDto.setAccountNumber(currentAccountTransaction.getAccountNumber());
            savingBankTransactionDto.setMember(currentAccountTransaction.getCurrentAccount().getMember());

            savingBankTransactionDto.setName(currentAccountTransaction.getCurrentAccount().getMember().getName());
            savingBankTransactionDto.setBalance(currentAccountTransaction.getBalance());
            if (currentAccountTransaction == null) {
                throw new EntityNotFoundException("CurrentAccountTransaction is not found or Not approved");
            }
            return new ResponseEntity(savingBankTransactionDto, HttpStatus.OK);
        } else if (savingBankTransactionDto.getDepositType().equalsIgnoreCase("CHILDRENS_DEPOSIT")) {
            Optional<ChildrensDeposit> childrensDepositObj = childrensDepositService.getChildrenDepositByAccountNumber(savingBankTransactionDto.getAccountNumber());
            ChildrensDeposit childrensDeposit = childrensDepositObj.get();
            savingBankTransactionDto.setAccountNumber(childrensDeposit.getAccountNumber());
            savingBankTransactionDto.setMember(childrensDeposit.getMember());
            savingBankTransactionDto.setName(childrensDeposit.getMember().getName());
            savingBankTransactionDto.setBalance(childrensDeposit.getMaturityAmount());
            if (childrensDeposit == null) {
                throw new EntityNotFoundException("ChildrensDeposit is not found or Not approved");
            }
            return new ResponseEntity(savingBankTransactionDto, HttpStatus.OK);
        } else if (savingBankTransactionDto.getDepositType().equalsIgnoreCase("DOUBLE_SCHEME")) {
            Optional<DoubleScheme> doubleSchemeObj = doubleSchemeService.getDoubleSchemeByAccountNumber(savingBankTransactionDto.getAccountNumber());
            DoubleScheme doubleScheme = doubleSchemeObj.get();
            savingBankTransactionDto.setAccountNumber(doubleScheme.getAccountNumber());
            savingBankTransactionDto.setName(doubleScheme.getMember().getName());
            savingBankTransactionDto.setMember(doubleScheme.getMember());
            savingBankTransactionDto.setBalance(doubleScheme.getMaturityAmount());
            if (doubleScheme == null) {
                throw new EntityNotFoundException("DoubleScheme is not found or Not approved");
            }
            return new ResponseEntity(savingBankTransactionDto, HttpStatus.OK);
        } else if (savingBankTransactionDto.getDepositType().equalsIgnoreCase("FIXED_DEPOSIT")) {
            Optional<FixedDeposit> fixedDepositObj = fixedDepositService.getFixedDepositByAccountNumber(savingBankTransactionDto.getAccountNumber());
            FixedDeposit fixedDeposit = fixedDepositObj.get();
            savingBankTransactionDto.setAccountNumber(fixedDeposit.getAccountNumber());
            savingBankTransactionDto.setMember(fixedDeposit.getMember());
            savingBankTransactionDto.setName(fixedDeposit.getMember().getName());
            savingBankTransactionDto.setBalance(fixedDeposit.getMaturityAmount());
            if (fixedDeposit == null) {
                throw new EntityNotFoundException("FixedDeposit is not found or Not approved");
            }
            return new ResponseEntity(savingBankTransactionDto, HttpStatus.OK);
        } else if (savingBankTransactionDto.getDepositType().equalsIgnoreCase("TERM_DEPOSIT")) {
            Optional<TermDeposit> termDepositObj = termDepositService.getTermDepositByAccountNumber(savingBankTransactionDto.getAccountNumber());
            TermDeposit termDeposit = termDepositObj.get();
            savingBankTransactionDto.setAccountNumber(termDeposit.getAccountNumber());
            savingBankTransactionDto.setMember(termDeposit.getMember());
            savingBankTransactionDto.setName(termDeposit.getMember().getName());
            savingBankTransactionDto.setBalance(termDeposit.getMaturityAmount());
            if (termDeposit == null) {
                throw new EntityNotFoundException("TermDeposit is not found or Not approved");
            }
            return new ResponseEntity(savingBankTransactionDto, HttpStatus.OK);
        } else if (savingBankTransactionDto.getDepositType().equalsIgnoreCase("PIGMY_DEPOSIT")) {
            PigmyDepositTransaction pigmyDepositTransaction = pigmyDepositTransactionService.getLatestTransaction(savingBankTransactionDto.getAccountNumber());
            savingBankTransactionDto.setAccountNumber(pigmyDepositTransaction.getPigmyDeposit().getAccountNumber());
            savingBankTransactionDto.setMember(pigmyDepositTransaction.getPigmyDeposit().getMember());
            savingBankTransactionDto.setName(pigmyDepositTransaction.getPigmyDeposit().getMember().getName());
            savingBankTransactionDto.setBalance(pigmyDepositTransaction.getBalance());
            if (pigmyDepositTransaction == null) {
                throw new EntityNotFoundException("pigmyDepositTransaction is not found or Not approved");
            }
            return new ResponseEntity(savingBankTransactionDto, HttpStatus.OK);
        } else if (savingBankTransactionDto.getDepositType().equalsIgnoreCase("LOAN")) {
            Optional<Loan> loanObj = loanService.findLoanByAccountNo(savingBankTransactionDto.getLoanAccountNumber());
            Loan loan = loanObj.get();

            Member member = memberService.getMemberByCustomer(loan.getLoanDetail().getCustomer());
            savingBankTransactionDto.setMember(member);
            savingBankTransactionDto.setName(loan.getMember().getName());
            savingBankTransactionDto.setBalance(loan.getLoanDetail().getBalanceAmount());
            if (!loanObj.isPresent()) {
                throw new EntityNotFoundException("Loan Not approved");
            }
            return new ResponseEntity(savingBankTransactionDto, HttpStatus.OK);
        } else if (savingBankTransactionDto.getDepositType().equalsIgnoreCase("RECURRING_DEPOSIT")) {
            Optional<RecurringDeposit> recurringDepositObj = recurringDepositService.getRecurringDepositByAccountNumber(savingBankTransactionDto.getAccountNumber());
            RecurringDeposit recurringDeposit = recurringDepositObj.get();
            savingBankTransactionDto.setAccountNumber(recurringDeposit.getAccountNumber());
            savingBankTransactionDto.setMember(recurringDeposit.getMember());
            savingBankTransactionDto.setName(recurringDeposit.getMember().getName());
            savingBankTransactionDto.setBalance(recurringDeposit.getBalance());
            if (recurringDeposit == null) {
                throw new EntityNotFoundException("Recurring Deposit is not found or Not approved");
            }
            return new ResponseEntity(savingBankTransactionDto, HttpStatus.OK);
        }
        return null;
    }

    private void transactionCreditEntry(SavingBankTransaction sbTransaction) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Amount Credited for SB Account " + sbTransaction.getId());
            creditTransaction.setCreditAmount(sbTransaction.getCreditAmount());
            creditTransaction.setTransactionBy(sbTransaction.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setAccountNumber(sbTransaction.getAccountNumber());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName, CREDIT);
            creditTransaction.setAccountHead(accountHead);
            creditTransaction.setTransferType("Cash");
            Transaction persistedDebitTransaction = transactionService.transactionEntry(creditTransaction);
        }
    }

    private void transactionDebitEntry(SavingBankTransaction sbTransaction) {
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(sbTransaction.getDebitAmount());
            debitTransaction.setRemark("Amount debited for SB Account " + sbTransaction.getId());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(sbTransaction.getTransactionBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setAccountNumber(sbTransaction.getAccountNumber());

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName, DEBIT);
            debitTransaction.setAccountHead(accountHead);
            debitTransaction.setTransferType("Cash");
            Transaction persistedDebitTransaction = transactionService.transactionEntry(debitTransaction);
        }
    }

    @GetMapping("/saving-bank-account-transactions/{accountNumber}")
    public ResponseEntity<List<SavingBankTransaction>> getSavingBankAccountTransactions(@PathVariable String accountNumber) {
        List<SavingBankTransaction> savingBankTransactions = null;
        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankDepositByAccountNumber(accountNumber);
        if (savingsBankDeposit != null) {
            savingBankTransactions = savingBankTransactionService.getSavingBankAccountTransactions(savingsBankDeposit);
        }
        if (CollectionUtils.isEmpty(savingBankTransactions)) {
            throw new EntityNotFoundException("No transactions found for this account");
        }
        return new ResponseEntity<>(savingBankTransactions, HttpStatus.OK);
    }
}
