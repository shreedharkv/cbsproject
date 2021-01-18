package com.dfq.coeffi.cbs.report.loanReports;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
public class GoldLoanReportDto {
    private String loanAccountNumber;
    private BigDecimal sanctionedAmount;
    private long customerId;
    private String name;
    private String familyMemberName;
    private String loanType;
    private BigDecimal totalRecoveredPrincipleAmount;
    private BigDecimal totalPrincipleAmountPending;
    private BigDecimal totalOverdueAmount;
    private Date overdueDate;
    private GoldLoanReportDto goldLoanReportDto;
    private BigDecimal totalInterestAmountPending;
    private BigDecimal totalPenalInterestPending;
    private Date sanctionedDate;
    private Date recoveryDate;

    private BigDecimal totalRecoveredPenalInterestAmount;
    private BigDecimal totalRecoveredInterestAmount;
    private BigDecimal totalOverdueInterestAmount;
    private BigDecimal totalOverduePenalInterestAmount;

    private BigDecimal balanceAmount;


}
