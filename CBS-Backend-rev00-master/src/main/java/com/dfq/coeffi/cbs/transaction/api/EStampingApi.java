package com.dfq.coeffi.cbs.transaction.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.transaction.entity.EStamping;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.EStampingService;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
public class EStampingApi extends BaseController {

    @Autowired
    private EStampingService eStampingService;

    @Autowired
    private BODDateService bodDateService;

    @Autowired
    private ApplicationLogService applicationLogService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountHeadService accountHeadService;

    @GetMapping("/estamping")
    public ResponseEntity<List<EStamping>> getTransactions() {

        List<EStamping> eStampings = eStampingService.getAllEStampingTransactions();
        if (CollectionUtils.isEmpty(eStampings)) {
            EStamping eStamping = new EStamping();
            eStamping.setTransactionOn(DateUtil.getTodayDate());
            eStamping.setBalance(BigDecimal.ZERO);
            eStampingService.eStampingEntry(eStamping);
        }
        Collections.reverse(eStampings);
        return new ResponseEntity<>(eStampings, HttpStatus.OK);
    }

    @PostMapping("/estamping-credit")
    public ResponseEntity<EStamping> saveEStampingCredit(@RequestBody EStamping eStamping, Principal principal) {

        bodDateService.checkBOD();
        User loggedUser = getLoggedUser(principal);

        EStamping persistedObject = null;

        Ledger ledger = accountHeadService.getLedgerByName("E-Stamping");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        EStamping latestEStampingTransaction = eStampingService.latestEStatmpingTransaction();

        if (latestEStampingTransaction != null) {

            // For credit E-Stamping transaction
            EStamping eStampingCredit = new EStamping();

            eStampingCredit.setCreditAmount(eStamping.getAmount());
            eStampingCredit.setDebitAmount(new BigDecimal(0));
            eStampingCredit.setRemark(eStamping.getRemark());
            eStampingCredit.setTransactionBy(loggedUser);
            eStampingCredit.setTransactionOn(DateUtil.getTodayDate());
            eStampingCredit.setTransactionType("CREDIT");
            eStampingCredit.setTransferType("EStamping");
            eStampingCredit.setAccountHead(ledger.getAccountHead());
            eStampingCredit.setLedger(ledger);

            BigDecimal balance = latestEStampingTransaction.getBalance();
            balance = balance.add(eStamping.getAmount());
            eStampingCredit.setBalance(balance);

            persistedObject = eStampingService.eStampingEntry(eStampingCredit);

            if (persistedObject != null) {

                bankAccountHeadDebitEntry(eStamping, loggedUser);
                eStampingCreditEntry(eStamping, loggedUser);

                String message = "" + persistedObject.getCreditAmount() + " Amount Credited to E-Stamping balance";
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), message,
                        "E-Stamping", loggedUser.getId());
            }
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }

    // BANK ACCOUNT HEAD DEBIT TRANSACTION
    private void bankAccountHeadDebitEntry(EStamping eStamping, User user) {

        Ledger ledger = accountHeadService.getLedgerByName("Bank Accounts");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For debit transaction
            Transaction debitTransaction = new Transaction();

            debitTransaction.setDebitAmount(eStamping.getAmount());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setRemark(eStamping.getRemark());
            debitTransaction.setTransactionBy(user);
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setTransferType("Bank");
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(eStamping.getAmount());
            debitTransaction.setBalance(balance);

            transactionService.transactionEntry(debitTransaction);

            if (debitTransaction != null) {
                String message = "" + debitTransaction.getCreditAmount() + " Amount debited for E-Stamping";
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "E-Stamping", user.getId());
            }
        }
    }

    // EStamping ACCOUNT HEAD CREDIT TRANSACTION
    private void eStampingCreditEntry(EStamping eStamping, User user) {

        Ledger ledger = accountHeadService.getLedgerByName("E-Stamping");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For credit transaction
            Transaction creditTransaction = new Transaction();

            creditTransaction.setCreditAmount(eStamping.getAmount());
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark(eStamping.getRemark());
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setTransferType("EStamping");
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(eStamping.getAmount());
            creditTransaction.setBalance(balance);

            transactionService.transactionEntry(creditTransaction);

            if (creditTransaction != null) {
                String message = "" + creditTransaction.getCreditAmount() + " Amount Credited to E-Stamping";
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "E-Stamping", user.getId());
            }
        }
    }

    @PostMapping("/estamping-debit")
    public ResponseEntity<EStamping> saveEStampingDebit(@RequestBody EStamping eStamping, Principal principal) {

        bodDateService.checkBOD();
        User loggedUser = getLoggedUser(principal);

        EStamping persistedObject = null;

        EStamping latestEStampingTransaction = eStampingService.latestEStatmpingTransaction();
        BigDecimal balance = latestEStampingTransaction.getBalance();
        if (eStamping.getAmount().intValue() > balance.intValue()) {
            throw new EntityNotFoundException("Amount should not be greater than E-Stamping balance");
        }

        BigDecimal commisionAMount = eStamping.getCommisionAmount();
        BigDecimal totalAmount = commisionAMount.add(eStamping.getAmount());

        Ledger ledger = accountHeadService.getLedgerByName("E-Stamping");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        if (latestEStampingTransaction != null) {

            // For debit E-Stamping transaction
            EStamping eStampingDebit = new EStamping();

            eStampingDebit.setDebitAmount(totalAmount);
            eStampingDebit.setCreditAmount(new BigDecimal(0));
            eStampingDebit.setRemark(eStamping.getRemark());
            eStampingDebit.setTransactionBy(loggedUser);
            eStampingDebit.setTransactionOn(DateUtil.getTodayDate());
            eStampingDebit.setTransactionType("DEBIT");
            eStampingDebit.setTransferType("EStamping");
            eStampingDebit.setAccountHead(ledger.getAccountHead());
            eStampingDebit.setLedger(ledger);
            eStampingDebit.setCommisionAmount(eStamping.getCommisionAmount());

            balance = balance.subtract(eStamping.getAmount().add(eStamping.getCommisionAmount()));
            eStampingDebit.setBalance(balance);

            persistedObject = eStampingService.eStampingEntry(eStampingDebit);

            if (persistedObject != null) {

                eStampingDebitEntry(eStamping, loggedUser, totalAmount);
                bankAccountHeadCreditEntry(eStamping, loggedUser, totalAmount);

                String message = "" + persistedObject.getDebitAmount() + " Amount Debited from E-Stamping balance";
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), message,
                        "E-Stamping", loggedUser.getId());
            }
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }

    // BANK ACCOUNT HEAD CREDIT TRANSACTION
    private void bankAccountHeadCreditEntry(EStamping eStamping, User user, BigDecimal totalAmount) {

        Ledger ledger = accountHeadService.getLedgerByName("Bank Accounts");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For credit transaction
            Transaction creditTransaction = new Transaction();

            creditTransaction.setCreditAmount(totalAmount);
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark(eStamping.getRemark());
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setTransferType("Bank");
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(totalAmount);
            creditTransaction.setBalance(balance);

            transactionService.transactionEntry(creditTransaction);

            if (creditTransaction != null) {
                String message = "" + creditTransaction.getCreditAmount() + " Amount credited to bank account head";
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "E-Stamping", user.getId());
            }
        }
    }

    // EStamping ACCOUNT HEAD DEBIT TRANSACTION
    private void eStampingDebitEntry(EStamping eStamping, User user, BigDecimal totalAmount) {

        Ledger ledger = accountHeadService.getLedgerByName("E-Stamping");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For credit transaction
            Transaction debitTransaction = new Transaction();

            debitTransaction.setDebitAmount(totalAmount);
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setRemark(eStamping.getRemark());
            debitTransaction.setTransactionBy(user);
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setTransferType("EStamping");
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(totalAmount);
            debitTransaction.setBalance(balance);

            transactionService.transactionEntry(debitTransaction);

            if (debitTransaction != null) {
                String message = "" + debitTransaction.getCreditAmount() + " Amount Debited From E-Stamping balance";
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "E-Stamping", user.getId());
            }
        }
    }

}