package com.dfq.coeffi.cbs.transaction.service;

import com.dfq.coeffi.cbs.transaction.entity.BankTransaction;
import java.util.Date;
import java.util.List;

public interface BankTransactionService {

    List<BankTransaction> getAllBankTransactions();
    BankTransaction bankTransactionEntry(BankTransaction bankTransaction);
    BankTransaction latestBankTransaction();
    List<BankTransaction> getAllBankTransactions(Date fromDate, Date toDate);
    List<BankTransaction> getAllBankTransactions(Date transactionDate);
    List<BankTransaction> getBankTransactionsByBetweenDates(Date dateFrom, Date dateTo);
}