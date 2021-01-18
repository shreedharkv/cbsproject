package com.dfq.coeffi.cbs.report.accountReports;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class LedgerReportDto {
    private String LedgerName;
    private String accountHeadName;
    private BigDecimal creditAmount;
    private BigDecimal debitAmount;
    private BigDecimal balanceAmount;
}
