package com.dfq.coeffi.cbs.deposit.Dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
public class PigmyDepositDto {

    private String accountNumber;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal maturityAmount;
    private Date maturityDate;
    private Date accountOpenDate;
    private BigDecimal periodOfDeposit;

}
