package com.dfq.coeffi.cbs.transaction.service;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import java.util.Date;
import java.util.List;

public interface TransactionService {

    List<Transaction> getAllTransactions();
    Transaction transactionEntry(Transaction transaction);
    Transaction latestTransaction();
    List<Transaction> getAllTransactions(Date fromDate, Date toDate);
    List<Transaction> getAllTransactions(Date transactionDate);
    List<Transaction> findByTransferType(String transferType);
    List<Transaction> getTransactionByDateAndTransferType(Date fromDate, Date toDate, String transferType);


    List<Transaction> getAllTransactions(Ledger ledger);
    List<Transaction> getAllTransactions(Date startDate,Date endDate,Ledger ledger);

    List<Transaction> getAllTransactions(AccountHead accountHead);
    List<Transaction> getAllTransactions(AccountHead accountHead, String transactionType);

    List<Transaction> getAllTransactions(Ledger ledger, String transactionType);


}