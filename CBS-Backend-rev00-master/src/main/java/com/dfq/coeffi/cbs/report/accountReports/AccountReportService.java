package com.dfq.coeffi.cbs.report.accountReports;

import com.dfq.coeffi.cbs.transaction.entity.Transaction;

import java.util.Date;
import java.util.List;

public interface AccountReportService {
    List<Transaction> getCashBookDetails(Date dateFrom, Date dateTo);
    List<Transaction> getDayBookDetails(Date dateFrom, Date dateTo);
    List<Transaction> getGeneralLedgerDetails(Date dateFrom, Date dateTo, String accountHeads);
    List<Transaction> getTrialBalance(Date dateFrom, Date dateTo);

    List<Transaction> getCashBookDetailsByLedger(Date dateFrom, Date dateTo, String ledger);
    List<Transaction> getDayBookDetailsByTransferType(Date dateFrom, Date dateTo, String transferType);
}