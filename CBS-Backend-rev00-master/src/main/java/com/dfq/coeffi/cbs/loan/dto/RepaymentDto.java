package com.dfq.coeffi.cbs.loan.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class RepaymentDto implements Serializable {

    private long creditAccountId;
    private String chequeNo;
    private Date chequeDate;
    private String remark;
    private String loanAccountNo;
    private String creditType;

    private BigDecimal interest;
    private BigDecimal penalInterest;
    private BigDecimal principal;
    private BigDecimal receivedAmount;

}