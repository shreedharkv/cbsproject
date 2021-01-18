package com.dfq.coeffi.cbs.report.bankReport;

import com.dfq.coeffi.cbs.bank.entity.BankMaster;

import java.util.Date;
import java.util.List;

public interface BankReportService {

    List<BankMaster> getBankDetails(String bankCodeFrom, String bankCodeTo);
   // List<BankMaster> getBankReconciliationReport(Date dateFrom,Date dateTo,String bankCode);
}
