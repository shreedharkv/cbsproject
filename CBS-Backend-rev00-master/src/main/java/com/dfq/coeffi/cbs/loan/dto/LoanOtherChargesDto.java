package com.dfq.coeffi.cbs.loan.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class LoanOtherChargesDto {

    private BigDecimal amount;
    private String chequeNo;
    private String debitType;
    private String remark;
    private String loanAccountNo;
    private long creditAccountId;
    private long debitAccountId;

}
