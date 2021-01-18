package com.dfq.coeffi.cbs.report.accountReports;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
public class GeneralLedgerDto {

    private BigDecimal creditAmount;
    private BigDecimal debitAmount;
    private BigDecimal balanceAmount;
    private String accountHead;
    private List<LedgerReportDto> ledgerReportDtos;

}
