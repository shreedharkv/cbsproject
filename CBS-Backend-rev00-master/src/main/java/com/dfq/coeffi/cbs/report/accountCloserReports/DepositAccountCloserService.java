package com.dfq.coeffi.cbs.report.accountCloserReports;

import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;

import java.util.List;

public interface DepositAccountCloserService {
    List<ChildrensDeposit> getAllChildrensDepositAccountCloser(int year);
    List<DoubleScheme> getAllDoubleSchemeAccountCloser(int year);
    List<RecurringDeposit> getAllRecurringDepositAccountCloser(int year);
    List<TermDeposit> getAllTermDepositAccountCloser(int year);
    List<FixedDeposit> getAllFixedDepositAccountCloser(int year);
    List<SavingsBankDeposit> getAllSavingsBankAccountCloser(int year);
    List<CurrentAccount> getAllCurrentAccountCloser(int year);

    List<Loan> getAllLoanCloser(int year, LoanType loanType);

}
