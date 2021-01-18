package com.dfq.coeffi.cbs.report.transactionReport;

import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class TransactionReportServiceImpl implements TransactionReportService {

    @Autowired
    private TransactionReportRepository transactionReportRepository;

    @Override
    public List<Transaction> getTransactionByAccountNumberAndBetweenDate(Date dateFrom, Date dateTo, String accountNumber) {
        return transactionReportRepository.getTransactionByAccountNumberAndBetweenDate(dateFrom, dateTo, accountNumber);
    }

    @Override
    public List<Transaction> getTransactionByAccountHeadAndBetweenDate(Date dateFrom, Date dateTo, String accountHead) {
        return transactionReportRepository.getTransactionByAccountHeadAndBetweenDate(dateFrom, dateTo, accountHead);
    }

    @Override
    public List<Transaction> getTransactionByLedgerAndBetweenDate(Date dateFrom, Date dateTo, String accountHead, String ledger) {
        return transactionReportRepository.getTransactionByLedgerAndBetweenDate(dateFrom, dateTo, accountHead, ledger);
    }

    @Override
    public List<Transaction> getTransactionByBetweenDate(Date dateFrom, Date dateTo) {
        return transactionReportRepository.getTransactionByBetweenDate(dateFrom, dateTo);
    }

    @Override
    public List<Transaction> getTransactionByDateAndLedgerAndAccountNumber(Date dateFrom, Date dateTo, String ledger, String accountNumber) {
        return transactionReportRepository.getTransactionByDateAndLedgerAndAccountNumber(dateFrom, dateTo, ledger, accountNumber);
    }
}