package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.master.entity.roi.loan.LoanRateOfInterest;

import java.util.List;

public interface LoanRateOfInterestService {

    List<LoanRateOfInterest> getLoanInterestTable();
    LoanRateOfInterest getRateOfInterestById(long id);
    LoanRateOfInterest getRateOfInterestByLoanTypeAndStatus(LoanType loanType);
    LoanRateOfInterest saveRateOfInterest(LoanRateOfInterest loanRateOfInterest);


}