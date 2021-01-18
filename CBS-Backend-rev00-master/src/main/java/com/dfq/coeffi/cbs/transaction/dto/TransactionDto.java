package com.dfq.coeffi.cbs.transaction.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TransactionDto {

    private String transferType;

    private Date dateFrom;

    private Date dateTo;
}
