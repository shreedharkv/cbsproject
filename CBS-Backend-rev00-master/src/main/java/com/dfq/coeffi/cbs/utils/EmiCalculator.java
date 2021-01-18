package com.dfq.coeffi.cbs.utils;

import com.dfq.coeffi.cbs.loan.entity.loan.LoanInstallments;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmiCalculator {

    private static DecimalFormat df2 = new DecimalFormat("#");

    public static List<LoanInstallments> calculateEmi(double p, double r, double n, Date repaymentDate) {

        List<LoanInstallments> loanInstallments = new ArrayList<>();
        double R = (r / 12) / 100;
        double P = p;
        double e = calcEmi(P, r, n);
        double totalInt = Math.round((e * n) - p);

        double intPerMonth = Math.round(totalInt / n);
        for (double i = 1; i <= n; i++) {
            intPerMonth = (P * R);
            P = ((P) - ((e) - (intPerMonth)));

            Date dueDate = DateUtil.addMonthsToGivenDate(repaymentDate, (int) i);
            LoanInstallments loanInstallment = new LoanInstallments();
            loanInstallment.setDueDate(dueDate);
            loanInstallment.setInstallmentNumber((int) i);
            loanInstallment.setInterestAmount(BigDecimal.valueOf(Math.round(intPerMonth)));
            loanInstallment.setPrincipleAmount(BigDecimal.valueOf(Math.round((e) - intPerMonth)));
            loanInstallment.setEmiAmount(BigDecimal.valueOf(Math.round(e)));
            loanInstallments.add(loanInstallment);

        }
        return loanInstallments;
    }

    public static Double calcEmi(double p, double r, double n) {
        double R = r;
        //double e = (p * R * (Math.pow((1 + R), n)) / ((Math.pow((1 + R), n)) - 1));\
        double e = p + (p * (R/100) * (n/12));
        e= e/n;
        return e;
    }

    public static Double calculateEmiOverdueInterest(Date paymentDueDate,Date payingDate,double emiAmount,double penalInterest) {
        double overdueInterest = 0;
        df2.setRoundingMode(RoundingMode.UP);
        if(paymentDueDate.before(payingDate)){
            penalInterest = penalInterest/100;
            double days = DateUtil.calculateDaysBetweenDate(paymentDueDate,payingDate);
            overdueInterest = days * emiAmount * (penalInterest/365);
            overdueInterest = Double.parseDouble(df2.format(overdueInterest));
        }
        return overdueInterest;
    }

    public static double pigmyInterestCalculation(double principle,double rate,double time){
        double compoundInterest = principle *(Math.pow((1 + rate / 100), time));
        double simpleInterest = (principle * time* rate) / 100;
        return simpleInterest;
    }

}