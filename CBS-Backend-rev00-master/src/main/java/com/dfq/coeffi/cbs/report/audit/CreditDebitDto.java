package com.dfq.coeffi.cbs.report.audit;

import com.dfq.coeffi.cbs.deposit.entity.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class CreditDebitDto {

    private String accountHead;
    private long accountCode;

    private BigDecimal subTotal;

    private TransactionType transactionType;

    private List<HeadDto> heads;

    private Date startDate;
    private Date endDate;
}