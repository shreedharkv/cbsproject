package com.dfq.coeffi.cbs.report.savingBankReports;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SavingBankReportDto {
    public String applicationNumberFrom;
    public String applicationNumberTo;
    public Date dateFrom;
    public Date dateTo;
    public Date inputDate;
    public String accountNumberFrom;
    public String accountNumberTo;
    public String reportType;
    public int inputMonth;
    public String accountNumber;
}
