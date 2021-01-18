package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.DepositInterestCalculation;
import com.dfq.coeffi.cbs.deposit.entity.FixedDepositInterestCalculation;

import java.util.Date;
import java.util.List;

public interface DepositInterestCalculationService {

    List<DepositInterestCalculation> getDepositInterestCalculations();
    DepositInterestCalculation saveDepositInterestCalculation(DepositInterestCalculation depositInterestCalculation);
    List<DepositInterestCalculation> getDepositInterestCalculationBetweenDates(Date dateFrom, Date dateTo, String accountNumber);
    List<DepositInterestCalculation> getDepositInterestCalculationByAccountNumber(String accountNumber);

    List<FixedDepositInterestCalculation> getFixedDepositInterestCalculations();
    FixedDepositInterestCalculation saveFixedDepositInterestCalculation(FixedDepositInterestCalculation fixedDepositInterestCalculation);
    List<FixedDepositInterestCalculation> getFixedDepositInterestCalculationBetweenDates(Date dateFrom, Date dateTo, String accountNumber);
}
