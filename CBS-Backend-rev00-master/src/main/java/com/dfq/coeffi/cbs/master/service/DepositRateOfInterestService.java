package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.deposit.entity.DepositType;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.master.entity.roi.fd.DepositRateOfInterest;
import com.dfq.coeffi.cbs.master.entity.roi.loan.LoanRateOfInterest;

import java.util.List;

public interface DepositRateOfInterestService {

    List<DepositRateOfInterest> getDepositInterestTable();
    DepositRateOfInterest getDepositRateOfInterestById(long id);
    DepositRateOfInterest getRateOfInterestByDepositTypeAndStatus(DepositType depositType);
    DepositRateOfInterest saveDepositRateOfInterest(DepositRateOfInterest depositRateOfInterest);

}