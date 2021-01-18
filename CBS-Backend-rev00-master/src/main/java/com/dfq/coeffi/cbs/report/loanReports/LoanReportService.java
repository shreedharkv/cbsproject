package com.dfq.coeffi.cbs.report.loanReports;

import com.dfq.coeffi.cbs.loan.entity.loan.Loan;

import java.util.Date;
import java.util.List;

public interface LoanReportService {

    List<Loan> getGoldLoanRecoveryDetailsByAccountNumber(String loanAccountNumberFrom, String loanAccountNumberTo, String exgLoanAccountNumberFrom, String exgLoanAccountNumberTo);
    List<Loan> getGoldLoanRegisterDetailsByAccountNumber(String loanAccountNumberFrom, String loanAccountNumberTo, String exgLoanAccountNumberFrom, String exgLoanAccountNumberTo);


    List<Loan> getAllLoanRecoveryDetail(String loanAccountNumberFrom, String loanAccountNumberTo);
    List<Loan> getAllLoanRecoveryDetailByExgAccount(String exgLoanAccountNumberFrom, String exgLoanAccountNumberTo);

}
