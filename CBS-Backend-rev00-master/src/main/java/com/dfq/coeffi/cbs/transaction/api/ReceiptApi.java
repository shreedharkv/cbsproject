package com.dfq.coeffi.cbs.transaction.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLog;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.depositTransaction.recurringDepositTransaction.RecurringDepositTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.recurringDepositTransaction.RecurringDepositTransactionService;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.AccountType;
import com.dfq.coeffi.cbs.deposit.entity.DepositType;
import com.dfq.coeffi.cbs.deposit.entity.RecurringDeposit;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import com.dfq.coeffi.cbs.deposit.service.RecurringDepositService;
import com.dfq.coeffi.cbs.deposit.service.SavingsBankDepositService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.member.entity.NumberFormat;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.transaction.entity.Receipt;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.ReceiptService;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.dfq.coeffi.cbs.utils.TransactionUtil;
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
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.CREDIT;
import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.DEBIT;

@RestController
@Slf4j
public class ReceiptApi extends BaseController {

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private SavingBankTransactionService savingBankTransactionService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ApplicationLogService applicationLogService;

    @Autowired
    private SavingsBankDepositService savingsBankDepositService;

    @Autowired
    private AccountHeadService accountHeadService;

    @Autowired
    private BODDateService bodDateService;

    @Autowired
    private RecurringDepositTransactionService recurringDepositTransactionService;

    @Autowired
    private RecurringDepositService recurringDepositService;

    @Autowired
    public BankService bankService;

    @Autowired
    public MemberService memberService;

    @PostMapping("receipt")
    public ResponseEntity<Receipt> createReceiptEntry(@Valid @RequestBody final Receipt receipt, Principal principal) {
        bodDateService.checkBOD();

        NumberFormat numberFormat = memberService.getNumberFormatByType("Receipt_Payment_Number");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need master data for generate member number and application number");
        }
        String paymentNumber = numberFormat.getPrefix() + "-" + (numberFormat.getPaymentNumber() + 1);

        SavingsBankDeposit sbAcc = null;

        if (receipt.getDepositType().equalsIgnoreCase("RECURRING_DEPOSIT")) {
            RecurringDepositTransaction latestRecord = recurringDepositTransactionService.getLatestTransactionOfRecurringDepositTransaction(receipt.getAccountNumber());
            if (latestRecord == null) {
                throw new EntityNotFoundException("Latest Transaction not found or RECURRING_DEPOSIT not approved");
            }
            Optional<RecurringDeposit> recurringDepositObj = recurringDepositService.getRecurringDepositByAccountNumber(receipt.getAccountNumber());

            BigDecimal latestBalanceAmount = latestRecord.getBalance();
            BigDecimal creditAmount = receipt.getCreditAmount();

            double totalPaidAmount = latestRecord.getBalance().doubleValue();
            double totalPrincipleAmount = recurringDepositObj.get().getDepositAmount().doubleValue()*recurringDepositObj.get().getNumberOfInstallments().doubleValue();
            if (recurringDepositObj.get().getDepositAmount().intValue() != creditAmount.intValue()) {
                throw new EntityNotFoundException("Installment Amount should be Rs:" + latestRecord.getDepositAmount());
            } else if (totalPaidAmount == totalPrincipleAmount) {
                throw new EntityNotFoundException("All Installment are paid Rs:" + totalPaidAmount);
            }
            if (receipt.getTransactionType().equalsIgnoreCase("CREDIT")) {
                receipt.setBalance(latestBalanceAmount.add(creditAmount));
                User loggedUser = getLoggedUser(principal);
                receipt.setTransactionBy(loggedUser);
                receipt.setPaymentNumber(paymentNumber);

                receiptService.createReceipt(receipt);

                RecurringDeposit recurringDeposit = recurringDepositObj.get();
                recurringDeposit.setBalance(latestBalanceAmount.add(creditAmount));
                recurringDepositService.saveRecurringDeposit(recurringDeposit);
                RecurringDepositTransaction rdTransaction = new RecurringDepositTransaction();
                rdTransaction.setAccountNumber(receipt.getAccountNumber());
                rdTransaction.setTransactionType(receipt.getTransactionType());
                rdTransaction.setBalance(receipt.getBalance());
                rdTransaction.setCreditAmount(receipt.getCreditAmount());
                rdTransaction.setTransferType(receipt.getTransferType());
                rdTransaction.setRecurringDeposit(recurringDeposit);
                rdTransaction.setTransactionOn(receipt.getTransactionOn());
                rdTransaction.setDebitAmount(BigDecimal.ZERO);
                rdTransaction.setVoucherType("RECEIPT");
                rdTransaction.setTransactionBy(loggedUser);
                rdTransaction.setDepositAmount(recurringDeposit.getDepositAmount());
                rdTransaction.setTotalPrincipleAmount(recurringDeposit.getDepositAmount().multiply(recurringDeposit.getNumberOfInstallments()));

                RecurringDepositTransaction persisted = recurringDepositTransactionService.createRecurringDepositTransaction(rdTransaction);
                if (persisted != null) {
                    transactionCreditEntryForRecurring(receipt, recurringDeposit);
                    TransactionUtil transactionUtil = new TransactionUtil(bankService);
                    transactionUtil.getUpdateSocietyBalance(persisted.getCreditAmount(), "CREDIT");
                }
            }
        } else if (receipt.getDepositType().equalsIgnoreCase("SAVING_BANK")) {
            SavingsBankDeposit savingsBankDeposit = null;
            SavingBankTransaction persistedDeposit = null;
            SavingBankTransaction latestRecord = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(receipt.getAccountNumber());
            if (latestRecord == null) {
                throw new EntityNotFoundException("Latest Transaction not found or SB Account not approved");
            }
            BigDecimal latestBalanceAmount = latestRecord.getBalance();
            BigDecimal creditAmount = receipt.getCreditAmount();

            if (receipt.getTransactionType().equalsIgnoreCase("CREDIT")) {
                receipt.setBalance(latestBalanceAmount.add(creditAmount));
                User loggedUser = getLoggedUser(principal);
                receipt.setTransactionBy(loggedUser);
                receipt.setPaymentNumber(paymentNumber);
                receipt.setDepositType("SAVING/CURRENT");

                receiptService.createReceipt(receipt);

                sbAcc = savingsBankDepositService.getSavingsBankDepositByAccountNumber(receipt.getAccountNumber());
                sbAcc.setBalance(receipt.getBalance());
                savingsBankDepositService.saveSavingsBankDeposit(sbAcc);
                SavingBankTransaction sbTransaction = new SavingBankTransaction();
                sbTransaction.setAccountNumber(receipt.getAccountNumber());
                sbTransaction.setTransactionType(receipt.getTransactionType());
                sbTransaction.setBalance(receipt.getBalance());
                sbTransaction.setCreditAmount(receipt.getCreditAmount());
                sbTransaction.setTransferType(receipt.getTransferType());
                sbTransaction.setDebitAmount(BigDecimal.ZERO);
                savingsBankDeposit = getSavingsBankDeposits(receipt.getAccountNumber());
                sbTransaction.setSavingsBankDeposit(savingsBankDeposit);
                sbTransaction.setTransactionBy(loggedUser);
                persistedDeposit = savingBankTransactionService.createSavingBankTransaction(sbTransaction);
                if (persistedDeposit != null) {
                    transactionCreditEntry(receipt, sbAcc);
                    TransactionUtil transactionUtil = new TransactionUtil(bankService);
                    transactionUtil.getUpdateSocietyBalance(persistedDeposit.getCreditAmount(), "CREDIT");
                }
            } else if (receipt.getTransactionType().equalsIgnoreCase("DEBIT")) {

                if(latestBalanceAmount.intValue() < creditAmount.intValue()){
                    throw new EntityNotFoundException("Debit amount is greater than SB balance amount");
                }
                receipt.setBalance(latestBalanceAmount.subtract(creditAmount));
                User loggedUser = getLoggedUser(principal);
                receipt.setTransactionBy(loggedUser);
                receipt.setPaymentNumber(paymentNumber);

                receiptService.createReceipt(receipt);

                sbAcc = savingsBankDepositService.getSavingsBankDepositByAccountNumber(receipt.getAccountNumber());
                sbAcc.setBalance(receipt.getBalance());
                savingsBankDepositService.saveSavingsBankDeposit(sbAcc);
                SavingBankTransaction sbTransaction = new SavingBankTransaction();
                sbTransaction.setAccountNumber(receipt.getAccountNumber());
                sbTransaction.setTransactionType(receipt.getTransactionType());
                sbTransaction.setBalance(receipt.getBalance());
                sbTransaction.setDebitAmount(receipt.getCreditAmount());
                sbTransaction.setCreditAmount(BigDecimal.ZERO);
                sbTransaction.setTransferType(receipt.getTransferType());
                savingsBankDeposit = getSavingsBankDeposits(receipt.getAccountNumber());
                sbTransaction.setTransactionBy(loggedUser);
                sbTransaction.setSavingsBankDeposit(savingsBankDeposit);
                persistedDeposit = savingBankTransactionService.createSavingBankTransaction(sbTransaction);

                if (persistedDeposit != null) {
                    transactionDebitEntry(receipt, sbAcc);
                    TransactionUtil transactionUtil = new TransactionUtil(bankService);
                    transactionUtil.getUpdateSocietyBalance(persistedDeposit.getCreditAmount(), "DEBIT");
                }
            }
            if (persistedDeposit != null) {
                numberFormat.setPaymentNumber(numberFormat.getPaymentNumber() + 1);
                memberService.updateNumberFormat(numberFormat);
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Saving Bank Receipt Created", "Saving Bank Receipt Received", loggedUser.getId());
            }
        }
        return new ResponseEntity<>(receipt, HttpStatus.CREATED);
    }

    private void transactionCreditEntry(Receipt receipt, SavingsBankDeposit savingsBankDeposit) {
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Amount credited towards Receipt Transaction " + receipt.getId());
            creditTransaction.setCreditAmount(receipt.getCreditAmount());
            creditTransaction.setTransactionBy(receipt.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setTransferType(receipt.getTransferType());
            creditTransaction.setAccountNumber(receipt.getAccountNumber());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);

            if (savingsBankDeposit != null && savingsBankDeposit.getMember().getName() != null && savingsBankDeposit.getMember().getMemberNumber() != null) {
                creditTransaction.setAccountName(savingsBankDeposit.getMember().getName() + " (" + savingsBankDeposit.getMember().getMemberNumber() + ")");
            }

            if (receipt.getDepositType().equalsIgnoreCase("RECURRING_DEPOSIT")) {
                Ledger ledger = accountHeadService.getLedgerByName("Recurring Deposit");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            } else {

                Ledger ledger = null;
                if (savingsBankDeposit.getAccountType().equals(AccountType.SAVING)) {
                    ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
                } else {
                    ledger = accountHeadService.getLedgerByName("Current Account");
                }
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            }
            transactionService.transactionEntry(creditTransaction);
        }
    }

    private void transactionCreditEntryForRecurring(Receipt receipt, RecurringDeposit recurringDeposit) {
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Amount credited towards Receipt Transaction " + receipt.getId());
            creditTransaction.setCreditAmount(receipt.getCreditAmount());
            creditTransaction.setTransactionBy(receipt.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setTransferType(receipt.getTransferType());
            creditTransaction.setAccountNumber(receipt.getAccountNumber());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);

            if (recurringDeposit != null && recurringDeposit.getMember().getName() != null && recurringDeposit.getMember().getMemberNumber() != null) {
                creditTransaction.setAccountName(recurringDeposit.getMember().getName() + " (" + recurringDeposit.getMember().getMemberNumber() + ")");
            }

            if (receipt.getDepositType().equalsIgnoreCase("RECURRING_DEPOSIT")) {
                Ledger ledger = accountHeadService.getLedgerByName("Recurring Deposit");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            }
            transactionService.transactionEntry(creditTransaction);
        }
    }

    private void transactionDebitEntry(Receipt receipt, SavingsBankDeposit savingsBankDeposit) {
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(receipt.getCreditAmount());
            creditTransaction.setRemark("Amount debited towards Receipt Transaction " + receipt.getId());
            creditTransaction.setCreditAmount(new BigDecimal(0));
            creditTransaction.setTransactionBy(receipt.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("DEBIT");
            creditTransaction.setTransferType(receipt.getTransferType());
            creditTransaction.setAccountNumber(receipt.getAccountNumber());

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(creditTransaction.getDebitAmount());
            creditTransaction.setBalance(balance);
            creditTransaction.setAccountName(savingsBankDeposit.getMember().getName() + " (" + savingsBankDeposit.getMember().getMemberNumber() + ")");

            if (receipt.getDepositType().equalsIgnoreCase("RECURRING_DEPOSIT")) {
                Ledger ledger = accountHeadService.getLedgerByName("Recurring Deposit");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            } else if (receipt.getDepositType().equalsIgnoreCase("SAVING_BANK")) {
                Ledger ledger = null;
                if (savingsBankDeposit.getAccountType().equals(AccountType.SAVING)) {
                    ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
                } else {
                    ledger = accountHeadService.getLedgerByName("Current Account");
                }
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            }
            transactionService.transactionEntry(creditTransaction);
        }
    }

    @GetMapping("receipt")
    public ResponseEntity<List<Receipt>> getAllReceiptEntries() {
        List<Receipt> receipts = receiptService.getAllReceipts();
        if (CollectionUtils.isEmpty(receipts)) {
            throw new EntityNotFoundException("receipts are not found");
        }
        return new ResponseEntity<>(receipts, HttpStatus.OK);
    }

    public SavingsBankDeposit getSavingsBankDeposits(String accountNumber) {
        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankDepositByAccountNumber(accountNumber);
        if (savingsBankDeposit == null) {
            throw new EntityNotFoundException("No Saving Bank Account in this A/c Number");
        }
        return savingsBankDeposit;
    }
}