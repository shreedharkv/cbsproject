package com.dfq.coeffi.cbs.expense.dto;

import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class ExpenseDto {

    private BigDecimal amount;

    private Ledger ledger;

    private Date fromDate;

    private Date toDate;
}