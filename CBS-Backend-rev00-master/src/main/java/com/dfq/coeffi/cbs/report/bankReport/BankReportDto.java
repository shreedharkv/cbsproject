package com.dfq.coeffi.cbs.report.bankReport;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class BankReportDto {

    public String bankCodeFrom;
    public String bankCodeTo;
    public String reportType;
    private Date dateFrom;
    private Date dateTo;
}