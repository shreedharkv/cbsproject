package com.dfq.coeffi.cbs.report.bankTransactionReport;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
public class BankTransactionReportDto {

    private Date dateFrom;

    private Date dateTo;
}