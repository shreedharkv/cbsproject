package com.dfq.coeffi.cbs.report.expenseReport;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
public class ExpenseDto {

    private Date dateFrom;

    private Date dateTo;
}