package com.dfq.coeffi.cbs.loan.service;

import com.dfq.coeffi.cbs.loan.entity.LoanAccountNumberFormat;

public interface LoanAccountNumberFormatService {

    LoanAccountNumberFormat getAccountNumberFormat();
    LoanAccountNumberFormat updateLoanAccountNumber(LoanAccountNumberFormat loanAccountNumberFormat);

}
