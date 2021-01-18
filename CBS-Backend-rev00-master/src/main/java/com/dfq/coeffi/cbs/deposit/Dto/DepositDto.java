package com.dfq.coeffi.cbs.deposit.Dto;

import com.dfq.coeffi.cbs.deposit.api.DepositNomineeDetails;
import com.dfq.coeffi.cbs.deposit.entity.DepositType;
import com.dfq.coeffi.cbs.deposit.entity.DepositsApproval;
import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;
import com.dfq.coeffi.cbs.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class DepositDto {

    private Member member;
    private long memberId;
    private DepositType depositType;
    private long depositId;
    private String memberNumber;
    private String applicationNumber;
    private String transactionNumber;
    private String accountNumber;
    private String exgAccountNumber;
    private BigDecimal depositAmount;
    private BigDecimal periodOfDeposit;
    private double rateOfInterest;
    private BigDecimal maturityAmount;
    private BigDecimal interestAmount;
    private BigDecimal balance;
    private Date maturityDate;
    private boolean status;
    private boolean isWithDrawn;
    private DepositsApproval depositsApproval;
    private DepositNomineeDetails depositNomineeDetails;
    private DepositNomineeDetails depositNomineeDetailsTwo;
    private DepositNomineeDetails depositNomineeDetailsThree;

    private PigmyAgent pigmyAgent;
    private BigDecimal numberOfInstallments;

    private String receiptNumber;
    private Date createdOn;
}