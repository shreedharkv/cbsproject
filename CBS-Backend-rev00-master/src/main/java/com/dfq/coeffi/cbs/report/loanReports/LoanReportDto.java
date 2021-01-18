package com.dfq.coeffi.cbs.report.loanReports;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanReportDto {
    public String loanAccountNumberFrom;
    public String loanAccountNumberTo;
    public String loanAccountNumber;
    public String exgLoanAccountNumberFrom;
    public String exgLoanAccountNumberTo;
    public String exgLoanAccountNumber;
    public String reportType;
}
