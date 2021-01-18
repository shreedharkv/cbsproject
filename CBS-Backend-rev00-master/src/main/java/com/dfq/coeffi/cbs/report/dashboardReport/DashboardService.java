package com.dfq.coeffi.cbs.report.dashboardReport;

import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanInstallments;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.MemberType;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;

import java.util.List;

public interface DashboardService {

    List<Transaction> getCashInHand();
    List<LoanInstallments> getNumberOfLoanInstallmentsDuesInCurrentWeek();
    List<FixedDeposit> getNumberOfDepositsRenewal();
    List<ChildrensDeposit> getNumberOfChildrensDepositsRenewal();
    List<DoubleScheme> getNumberOfDoubleSchemesRenewal();
    List<RecurringDeposit> getNumberOfRecurringDepositsRenewal();
    List<TermDeposit> getNumberOfTermDepositsRenewal();

    List<SavingsBankDeposit> getNumberOfSavingBankAccounts();

    List<ChildrensDeposit> getAllApprovedDeposits();
    List<DoubleScheme> getAllApprovedDoubleScheme();
    List<FixedDeposit> getAllApprovedFixedDeposit();
    List<TermDeposit> getAllApprovedTermDeposit();
    List<RecurringDeposit> getAllApprovedRecurringDeposit();
}
