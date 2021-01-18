package com.dfq.coeffi.cbs.report.loanReports;

import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoanMemberDto {

    private Member member;
    private Loan loan;
    private GoldLoanReportDto goldLoanReportDto;
}