package com.dfq.coeffi.cbs.report.bankReport;

import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BankReportServiceImpl implements BankReportService {

    private final BankReportRepository bankReportRepository;

    @Autowired
    public BankReportServiceImpl(BankReportRepository bankReportRepository){
        this.bankReportRepository = bankReportRepository;
    }
    @Override
    public List<BankMaster> getBankDetails(String bankCodeFrom,String bankCodeTo) {
        return bankReportRepository.getBankDetails(bankCodeFrom,bankCodeTo);
    }

   /* @Override
    public List<BankMaster> getBankReconciliationReport(Date dateFrom,Date dateTo,String bankCode) {
        return bankReportRepository.getBankReconciliationReport(dateFrom,dateTo,bankCode);
    }*/
}
