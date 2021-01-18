package com.dfq.coeffi.cbs.report.transactionReport;

import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import java.util.Date;
import java.util.List;

public interface TransactionReportService {

    List<Transaction> getTransactionByAccountNumberAndBetweenDate(Date dateFrom, Date dateTo, String accountNumber);

    List<Transaction> getTransactionByAccountHeadAndBetweenDate(Date dateFrom, Date dateTo, String accountHead);

    List<Transaction> getTransactionByLedgerAndBetweenDate(Date dateFrom, Date dateTo, String accountHead, String ledger);

    List<Transaction> getTransactionByBetweenDate(Date dateFrom, Date dateTo);

    List<Transaction> getTransactionByDateAndLedgerAndAccountNumber(Date dateFrom, Date dateTo, String ledger, String accountNumber);
}