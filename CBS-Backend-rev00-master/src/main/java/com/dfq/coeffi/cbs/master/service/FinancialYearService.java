package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import com.dfq.coeffi.cbs.master.entity.roi.loan.LoanRateOfInterest;

import java.util.List;

public interface FinancialYearService {

    List<FinancialYear> getFinancialYears();
    FinancialYear getCurrentFinancialYear();
    FinancialYear createNewFinancialYear(FinancialYear financialYear);


}