package com.dfq.coeffi.cbs.report.transactionReport;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
public class TransactionReportDto {

    private Date dateFrom;

    private Date dateTo;

    private String accountNumber;

    private String accountHead;

    private String ledger;
}