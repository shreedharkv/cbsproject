package com.dfq.coeffi.cbs.transaction.api;

import com.dfq.coeffi.cbs.admin.entity.BODDate;
import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.transaction.entity.BankTransaction;
import com.dfq.coeffi.cbs.transaction.entity.EodTransaction;
import com.dfq.coeffi.cbs.transaction.service.BankTransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.dfq.coeffi.cbs.utils.TransactionValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class BankTransactionApi extends BaseController {

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private BODDateService bodDateService;

    @Autowired
    private BankService bankService;

    @Autowired
    private ApplicationLogService applicationLogService;

    @Autowired
    private AccountHeadService accountHeadService;

    @GetMapping("/bank-transaction/account")
    public ResponseEntity<List<BankTransaction>> getBankTransactions() {
        List<BankTransaction> bankTransactions = bankTransactionService.getAllBankTransactions();
        if (CollectionUtils.isEmpty(bankTransactions)) {
            throw new EntityNotFoundException("Bank Transactions not found");
        }
        return new ResponseEntity<>(bankTransactions, HttpStatus.OK);
    }

    @PostMapping("/bank-transaction/{bankId}")
    public ResponseEntity<BankTransaction> saveBankTransaction(@RequestBody BankTransaction bankTransaction, @PathVariable long bankId, Principal principal) {

        bodDateService.checkBOD();
        User loggedUser = getLoggedUser(principal);

        Optional<BankMaster> bankMasterObj = bankService.getBankMaster(bankId);
        if (!bankMasterObj.isPresent()) {
            log.warn("No active bank found");
            throw new EntityNotFoundException(BankMaster.class.getName());
        }
        BankMaster bankMaster = bankMasterObj.get();

        bankTransaction.setBankMaster(bankMaster);

        if(bankTransaction.getTransactionType().equalsIgnoreCase("Debit")){
            bankTransactionDebitEntry(bankTransaction, bankMaster, loggedUser);
        }else if(bankTransaction.getTransactionType().equalsIgnoreCase("Credit")){
            bankTransactionCreditEntry(bankTransaction, bankMaster, loggedUser);
        }else if(bankTransaction.getTransactionType().equalsIgnoreCase("Interest")){
            bankTransactionInterestEntry(bankTransaction, bankMaster, loggedUser);
        }

        applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Bank Transaction Id " + bankTransaction.getId() + " submitted",
                "BANK TRANSACTION SUBMIT", loggedUser.getId());

        return new ResponseEntity<>(bankTransaction, HttpStatus.OK);
    }

    // BANK TRANSACTION DEBIT ENTRY
    private void bankTransactionDebitEntry(BankTransaction bankTransaction, BankMaster bankMaster, User user) {

        Ledger ledger = accountHeadService.getLedgerByName("External Bank Accounts");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }
        TransactionValidation.checkSocietyBalance(bankTransaction.getAmount(),bankMaster.getBalance());

        BankTransaction latestBankTransaction = bankTransactionService.latestBankTransaction();

        if (latestBankTransaction != null) {

            // For debit transaction against the bank
            BankTransaction debitTransaction = new BankTransaction();

            debitTransaction.setDebitAmount(bankTransaction.getAmount());
            debitTransaction.setRemark("Amount debited from " + bankMaster.getBankName() + " : " + bankMaster.getAccountNumber());
            debitTransaction.setTransactionBy(user);
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setAccountNumber(bankMaster.getAccountNumber());
            debitTransaction.setTransferType(bankTransaction.getModeOfPayment());
            debitTransaction.setBankMaster(bankMaster);
            debitTransaction.setAccountHead(bankTransaction.getAccountHead());
            debitTransaction.setLedger(ledger);
            debitTransaction.setDescription(bankTransaction.getDescription());

            BigDecimal bankTransactionBalance = latestBankTransaction.getBalance();
            bankTransactionBalance = bankTransactionBalance.subtract(bankTransaction.getAmount());
            debitTransaction.setBalance(bankTransactionBalance);

            bankTransactionService.bankTransactionEntry(debitTransaction);

            BigDecimal bankMasterBalance = bankMaster.getBalance();
            bankMasterBalance = bankMasterBalance.subtract(bankTransaction.getAmount());
            bankMaster.setBalance(bankMasterBalance);

            bankService.saveBankMaster(bankMaster);

            if (debitTransaction != null) {
                String message = "" + debitTransaction.getCreditAmount() + " Amount debited from" + bankMaster.getBankName() + bankMaster.getAccountNumber();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "BANK TRANSACTION", user.getId());
            }
        }
    }

    // BANK TRANSACTION DEBIT ENTRY
    private void bankTransactionCreditEntry(BankTransaction bankTransaction, BankMaster bankMaster, User user) {

        Ledger ledger = accountHeadService.getLedgerByName("External Bank Accounts");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }
        BankTransaction latestBankTransaction = bankTransactionService.latestBankTransaction();

        // For credit transaction against the bank
        BankTransaction creditTransaction = new BankTransaction();

        creditTransaction.setCreditAmount(bankTransaction.getAmount());
        creditTransaction.setRemark("Amount credited to " + bankMaster.getBankName() + " : " + bankMaster.getAccountNumber());
        creditTransaction.setTransactionBy(user);
        creditTransaction.setTransactionOn(DateUtil.getTodayDate());
        creditTransaction.setTransactionType("CREDIT");
        creditTransaction.setDebitAmount(new BigDecimal(0));
        creditTransaction.setAccountNumber(bankMaster.getAccountNumber());
        creditTransaction.setTransferType(bankTransaction.getModeOfPayment());
        creditTransaction.setBankMaster(bankMaster);
        creditTransaction.setAccountHead(bankTransaction.getAccountHead());
        creditTransaction.setLedger(ledger);
        creditTransaction.setDescription(bankTransaction.getDescription());

        BigDecimal bankTransactionBalance = latestBankTransaction.getBalance();
        bankTransactionBalance = bankTransactionBalance.add(bankTransaction.getAmount());
        creditTransaction.setBalance(bankTransactionBalance);

        BankTransaction transaction = bankTransactionService.bankTransactionEntry(creditTransaction);

        BigDecimal balanceAmount = bankMaster.getBalance();
        balanceAmount = balanceAmount.add(bankTransaction.getAmount());
        bankMaster.setBalance(balanceAmount);

        bankService.saveBankMaster(bankMaster);

        if (transaction != null) {
            String message = "" + transaction.getCreditAmount() + " Amount credited to " + bankMaster.getBankName() + bankMaster.getAccountNumber();
            applicationLogService.recordApplicationLog(user.getFirstName(), message,
                    "BANK TRANSACTION", user.getId());
        }
    }

    private void bankTransactionInterestEntry(BankTransaction bankTransaction, BankMaster bankMaster, User user) {

        Ledger ledger = accountHeadService.getLedgerByName("External Bank Accounts Interest");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger(External Bank Accounts Interest) not found");
        }
        BankTransaction latestBankTransaction = bankTransactionService.latestBankTransaction();

        // For credit transaction against the bank
        BankTransaction creditTransaction = new BankTransaction();

        creditTransaction.setCreditAmount(bankTransaction.getAmount());
        creditTransaction.setRemark("Amount credited to " + bankMaster.getBankName() + " : " + bankMaster.getAccountNumber());
        creditTransaction.setTransactionBy(user);
        creditTransaction.setTransactionOn(DateUtil.getTodayDate());
        creditTransaction.setTransactionType("INTEREST");
        creditTransaction.setDebitAmount(new BigDecimal(0));
        creditTransaction.setAccountNumber(bankMaster.getAccountNumber());
        creditTransaction.setTransferType(bankTransaction.getModeOfPayment());
        creditTransaction.setBankMaster(bankMaster);
        creditTransaction.setAccountHead(bankTransaction.getAccountHead());
        creditTransaction.setLedger(ledger);
        creditTransaction.setDescription(bankTransaction.getDescription());

        BigDecimal bankTransactionBalance = latestBankTransaction.getBalance();
        bankTransactionBalance = bankTransactionBalance.add(bankTransaction.getAmount());
        creditTransaction.setBalance(bankTransactionBalance);

        BankTransaction transaction = bankTransactionService.bankTransactionEntry(creditTransaction);

        BigDecimal balanceAmount = bankMaster.getBalance();
        balanceAmount = balanceAmount.add(bankTransaction.getAmount());
        bankMaster.setBalance(balanceAmount);

        bankService.saveBankMaster(bankMaster);

        if (transaction != null) {
            String message = "" + transaction.getCreditAmount() + " Amount credited to " + bankMaster.getBankName() + bankMaster.getAccountNumber();
            applicationLogService.recordApplicationLog(user.getFirstName(), message,
                    "BANK TRANSACTION", user.getId());
        }
    }

    @GetMapping("/bod-bank-transaction")
    public ResponseEntity<EodTransaction> getBodTransaction() {

        EodTransaction eodTransaction = new EodTransaction();

        BigDecimal creditAmount = new BigDecimal(0);
        BigDecimal debitAmount = new BigDecimal(0);
        BigDecimal balanceAsOn = new BigDecimal(0);
        BigDecimal closingBalance = new BigDecimal(0);

        Optional<BODDate> bodDateObj = bodDateService.getBODDateByStatus();
        if (!bodDateObj.isPresent()) {
            throw new EntityNotFoundException("Bod not started yet");
        }

        BODDate bodDate = bodDateObj.get();
        List<BankTransaction> bankTransactions = bankTransactionService.getAllBankTransactions(bodDate.getBodDate());
        List<BankTransaction> yesterdayBankTransactions = bankTransactionService.getAllBankTransactions(DateUtil.getYesterdayDate());
        if (bankTransactions != null && bankTransactions.size() > 0) {
            for (BankTransaction bankTransaction : bankTransactions) {
                creditAmount = creditAmount.add(bankTransaction.getCreditAmount());
                debitAmount = debitAmount.add(bankTransaction.getDebitAmount());
            }
            Collections.reverse(bankTransactions);
            balanceAsOn = bankTransactions.get(0).getBalance();
        }

        if (yesterdayBankTransactions != null && yesterdayBankTransactions.size() > 0) {
            Collections.reverse(yesterdayBankTransactions);
            closingBalance = yesterdayBankTransactions.get(0).getBalance();
        }

        eodTransaction.setCreditAmount(creditAmount);
        eodTransaction.setDebitAmount(debitAmount);
        eodTransaction.setBalanceAsOn(balanceAsOn);
        eodTransaction.setClosingBalance(balanceAsOn);

        return new ResponseEntity<>(eodTransaction, HttpStatus.OK);
    }

    @GetMapping("/latest-bank-transaction")
    public ResponseEntity<BankTransaction> getLatestBankTransactions() {
        BankTransaction bankTransactions = bankTransactionService.latestBankTransaction();
        if(bankTransactions == null){
            throw new EntityNotFoundException("Bank transaction not found");
        }
        return new ResponseEntity<>(bankTransactions, HttpStatus.OK);
    }
}