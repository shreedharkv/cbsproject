package com.dfq.coeffi.cbs.report.depositReports;

import com.dfq.coeffi.cbs.deposit.entity.DepositType;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class DepositReportDto {
    public Date inputDate;
    public DepositType depositType;
    public String reportType;
    public String accountNumber;
    public long customerId;
    public String accountNumberFrom;
    public String accountNumberTo;
    public Date dateFrom;
    public Date dateTo;
    public String accountHeads;
    public BigDecimal preMatureFine;
    public int year;
    public Date maturityDateFrom;
    public Date maturityDateTo;
    public LoanType loanType;
    private String modeOfPayment;
    private String pigmyAccNumber;
    private BigDecimal amount;
    private String transferType;

    private String ledger;
    private Date asOnDate;
}