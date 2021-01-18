package com.dfq.coeffi.cbs.deposit.Dto;


import com.dfq.coeffi.cbs.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class SavingBankTransactionDto {
    private String accountNumber;
    private String depositType;

    private String loanAccountNumber;

    private BigDecimal balance;
    private String name;

    private Member member;
}
