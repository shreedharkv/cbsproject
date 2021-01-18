package com.dfq.coeffi.cbs.transaction.service;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction transactionEntry(Transaction transaction) {

        if (transaction.getOtherFees() == null) {
            transaction.setOtherFees(BigDecimal.ZERO);
        }

        Transaction latestTransaction = latestTransaction();
        if (transaction.getTransferType().equalsIgnoreCase("CASH") || transaction.getTransferType().equalsIgnoreCase("Cash")) {
            if (transaction.getTransactionType().equalsIgnoreCase("CREDIT")) {
                transaction.setCashBalance(latestTransaction.getCashBalance().add(transaction.getCreditAmount()));
            } else if(transaction.getTransactionType().equalsIgnoreCase("DEBIT")){
                transaction.setCashBalance(latestTransaction.getCashBalance().subtract(transaction.getDebitAmount()));
            }
        } else {
            transaction.setCashBalance(latestTransaction.getCashBalance());
        }
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction latestTransaction() {
        Transaction latestTransaction = null;
        List<Transaction> transactions = transactionRepository.findAll();
        if (transactions != null && transactions.size() > 0) {
            Collections.reverse(transactions);
            latestTransaction = transactions.get(0);
        }
        return latestTransaction;
    }

    @Override
    public List<Transaction> getAllTransactions(Date fromDate, Date toDate) {
        return transactionRepository.getTransactionByBetweenDates(fromDate, toDate);
    }

    @Override
    public List<Transaction> getAllTransactions(Date transactionDate) {
        return transactionRepository.findByTransactionOn(transactionDate);
    }

    @Override
    public List<Transaction> findByTransferType(String transferType) {
        return transactionRepository.findByTransferType(transferType);
    }

    @Override
    public List<Transaction> getTransactionByDateAndTransferType(Date fromDate, Date toDate, String transferType) {
        return transactionRepository.getTransactionByDateAndTransferType(fromDate, toDate, transferType);
    }

    @Override
    public List<Transaction> getAllTransactions(Ledger ledger) {
        return transactionRepository.findByLedger(ledger);
    }

    @Override
    public List<Transaction> getAllTransactions(Date startDate, Date endDate, Ledger ledger) {
        return transactionRepository.getAllTransactions(startDate,endDate,ledger);
    }

    @Override
    public List<Transaction> getAllTransactions(AccountHead accountHead) {
        return transactionRepository.findByAccountHead(accountHead);
    }

    @Override
    public List<Transaction> getAllTransactions(AccountHead accountHead, String transactionType) {
        return transactionRepository.findByAccountHeadAndTransactionType(accountHead, transactionType);
    }

    @Override
    public List<Transaction> getAllTransactions(Ledger ledger, String transactionType) {
        return transactionRepository.findByLedgerAndTransactionType(ledger, transactionType);
    }
}