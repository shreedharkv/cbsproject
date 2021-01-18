package com.dfq.coeffi.cbs.loan.dto;

import com.dfq.coeffi.cbs.loan.entity.loan.LoanEmiStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
public class EmiOverdueDto {

    private long emiNumber;
    private BigDecimal principleAmount;
    private BigDecimal emiAmount;
    private double overDueInterest;
    private String loanAccountNumber;
    private BigDecimal interestAmount;
    private BigDecimal penalInterestAmount;
    private LoanEmiStatus loanEmiStatus;
    private Date dueDate;

    private long id;

    private BigDecimal totalPenalInterest;
    private BigDecimal totalPrincipleAmount;
    private BigDecimal totalInterestAmount;
    private BigDecimal totalAmount;

    private BigDecimal totalRecoveredPrincipleAmount;
    private BigDecimal totalInterestAmountPending;
    private BigDecimal totalPenalInterestPending;
    private BigDecimal totalPrincipleAmountPending;
    private BigDecimal totalOverdueAmount;
    private Date overdueDate;
    private Date recoveryDate;

    private BigDecimal totalRecoveredPenalInterestAmount;
    private BigDecimal totalRecoveredInterestAmount;
    private BigDecimal totalOverdueInterestAmount;
    private BigDecimal totalOverduePenalInterestAmount;

    private BigDecimal overdueAmount;

}
