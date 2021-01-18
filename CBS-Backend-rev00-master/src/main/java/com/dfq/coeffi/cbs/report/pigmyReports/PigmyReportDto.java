package com.dfq.coeffi.cbs.report.pigmyReports;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class PigmyReportDto {
    public String applicationFrom;
    public String applicationTo;
    public Date dateFrom;
    public Date dateTo;
    public Date inputDate;
    public String accountNumberFrom;
    public String accountNumberTo;
    public String reportType;
    public int inputMonth;
    public String accountNumber;

}
