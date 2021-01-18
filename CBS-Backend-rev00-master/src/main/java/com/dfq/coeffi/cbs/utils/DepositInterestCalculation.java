package com.dfq.coeffi.cbs.utils;

import com.dfq.coeffi.cbs.deposit.entity.TransactionType;

import java.math.BigDecimal;

public class DepositInterestCalculation {

    public static BigDecimal calculateInterest(BigDecimal depositAmount, BigDecimal periodOfDeposit, double rateOfInterest){
        BigDecimal rate = new BigDecimal(rateOfInterest);
        BigDecimal percent = new BigDecimal(100);
        BigDecimal interest = depositAmount.multiply(periodOfDeposit.multiply(rate)).divide(percent);
        return interest;
    }

    public static BigDecimal calculateMaturityAmount(BigDecimal depositAmount,BigDecimal interestAmount){
        BigDecimal maturityAmount = depositAmount.add(interestAmount);
        return maturityAmount;
    }

    public static double calculateRecurringDepositInterest(BigDecimal depositAmount, BigDecimal numberOfInstallments, double rateOfInterest){
        BigDecimal bd = depositAmount;
        double p = bd.doubleValue();
        BigDecimal installments = numberOfInstallments;
        double n = installments.doubleValue();
        double r = rateOfInterest;
        double interestAmount;
        interestAmount =  p*(n*(n+1)/2)*(r/100)*(1/12.0);
        return interestAmount;
    }

    public static double calculateRecurringDepositMaturityAmount(BigDecimal depositAmount, BigDecimal numberOfInstallments,double rateOfInterest){
        BigDecimal bd = depositAmount; // the value you get
        double p = bd.doubleValue();
        BigDecimal installments = numberOfInstallments; // the value you get
        double n = installments.doubleValue();
        double maturityAmount;
        maturityAmount =  p*n + rateOfInterest;
        return maturityAmount;
    }

    public static double getBalanceAmount(long accountNumber, TransactionType transactionType,double amount,double balanceAmount){
        double amoun = 0.0;
        return amoun;
    }

}
