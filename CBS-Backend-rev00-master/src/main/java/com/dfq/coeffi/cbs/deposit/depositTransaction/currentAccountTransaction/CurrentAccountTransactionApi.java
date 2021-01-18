package com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction;

import com.dfq.coeffi.cbs.deposit.Dto.SavingBankTransactionDto;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;

@RestController
@Slf4j
@Transactional
public class CurrentAccountTransactionApi extends BaseController {

    @Autowired
    private CurrentAccountTransactionService currentAccountTransactionService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping("current-account-transaction")
    public ResponseEntity<CurrentAccountTransaction> createCurrentAccountTransaction(@Valid @RequestBody final CurrentAccountTransaction currentAccountTransaction,Principal principal) {
        CurrentAccountTransaction persistedDeposit = null;
        CurrentAccountTransaction latestRecord = currentAccountTransactionService.getLatestTransactionOfCurrentAccount(currentAccountTransaction.getCurrentAccount().getAccountNumber());
        BigDecimal latestBalanceAmount = latestRecord.getBalance();
        BigDecimal creditAmount = currentAccountTransaction.getCreditAmount();
        if (currentAccountTransaction.getTransactionType().equalsIgnoreCase("CREDIT")) {
            currentAccountTransaction.setBalance(latestBalanceAmount.add(creditAmount));
            currentAccountTransaction.setVoucherType("RECEIPT");
            currentAccountTransaction.setDebitAmount(BigDecimal.ZERO);
            persistedDeposit = currentAccountTransactionService.createCurrentAccountTransaction(currentAccountTransaction);
            if(persistedDeposit!=null) {
                Transaction transaction = new Transaction();
                transaction.setBalance(currentAccountTransaction.getBalance());
                transaction.setCreditAmount(currentAccountTransaction.getCreditAmount());
                transaction.setCreatedOn(currentAccountTransaction.getCreatedOn());
                transaction.setTransactionType(currentAccountTransaction.getTransactionType());
                transaction.setTransactionBy(currentAccountTransaction.getTransactionBy());
                transaction.setVoucherType(currentAccountTransaction.getVoucherType());
                transaction.setParticulars("CURRENT ACCOUNT DEPOSIT");
                transaction.setTransactionOn(currentAccountTransaction.getCreatedOn());
                transaction.setTransferType("Cash");
                transactionService.transactionEntry(transaction);
            }
        } else if ((currentAccountTransaction.getTransactionType().equalsIgnoreCase("DEBIT") && (currentAccountTransaction.getDebitAmount().compareTo(latestRecord.getBalance()) <= 0))) {
            currentAccountTransaction.setBalance(latestBalanceAmount.subtract(currentAccountTransaction.getDebitAmount()));
            currentAccountTransaction.setVoucherType("PAYMENT");
            currentAccountTransaction.setCreditAmount(BigDecimal.ZERO);
            persistedDeposit = currentAccountTransactionService.createCurrentAccountTransaction(currentAccountTransaction);
            if(persistedDeposit!=null) {
                Transaction transaction = new Transaction();
                transaction.setBalance(currentAccountTransaction.getBalance());
                transaction.setDebitAmount(currentAccountTransaction.getDebitAmount());
                transaction.setCreatedOn(currentAccountTransaction.getCreatedOn());
                transaction.setTransactionType(currentAccountTransaction.getTransactionType());
                transaction.setTransactionBy(currentAccountTransaction.getTransactionBy());
                transaction.setVoucherType(currentAccountTransaction.getVoucherType());
                transaction.setParticulars("CURRENT ACCOUNT DEPOSIT");
                transaction.setTransactionOn(currentAccountTransaction.getCreatedOn());
                transaction.setTransferType("Cash");
                transactionService.transactionEntry(transaction);
            }
        } else {
            log.warn("Withdraw amount is morethan balance amount");
        }
        return new ResponseEntity<>(persistedDeposit,HttpStatus.CREATED);
    }

    @PostMapping("current-account-transaction/latest")
    public ResponseEntity<CurrentAccountTransaction> getCurrentAccountTransactionByAccountNumber(@Valid @RequestBody SavingBankTransactionDto savingBankTransactionDto) {
        CurrentAccountTransaction latestTransactionOfCA = currentAccountTransactionService.getLatestTransactionOfCurrentAccount(savingBankTransactionDto.getAccountNumber());
        if (latestTransactionOfCA == null) {
            throw new EntityNotFoundException("CA is not found or Not approved");
        }
        return new ResponseEntity(latestTransactionOfCA, HttpStatus.OK);
    }

}