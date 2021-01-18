package com.dfq.coeffi.cbs.report.accountReports;

import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AccountReportServiceImpl implements AccountReportService{

    @Autowired
    private AccountReportRepository accountReportRepository;

   @Override
    public List<Transaction> getCashBookDetails(Date dateFrom,Date dateTo) {
       return accountReportRepository.getCashBookDetails(dateFrom,dateTo);
    }

    @Override
    public List<Transaction> getDayBookDetails(Date dateFrom,Date dateTo) {
        return accountReportRepository.getDayBookDetails(dateFrom,dateTo);
    }

    @Override
    public List<Transaction> getGeneralLedgerDetails(Date dateFrom,Date dateTo,String accountHeads) {
        return accountReportRepository.getGeneralLedgerDetails(dateFrom,dateTo,accountHeads);
    }

    @Override
    public List<Transaction> getTrialBalance(Date dateFrom, Date dateTo) {
        return accountReportRepository.getTrialBalance(dateFrom,dateTo);
    }

    @Override
    public List<Transaction> getCashBookDetailsByLedger(Date dateFrom, Date dateTo, String ledger) {
        return accountReportRepository.getCashBookDetailsByLedger(dateFrom, dateTo, ledger);
    }

    @Override
    public List<Transaction> getDayBookDetailsByTransferType(Date dateFrom, Date dateTo, String transferType) {
        return accountReportRepository.getDayBookDetailsByTransferType(dateFrom, dateTo, transferType);
    }
}
