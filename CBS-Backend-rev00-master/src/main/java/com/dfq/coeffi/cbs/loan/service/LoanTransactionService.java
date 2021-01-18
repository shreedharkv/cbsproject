package com.dfq.coeffi.cbs.loan.service;

import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanTransaction;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;

import java.util.List;

public interface LoanTransactionService {

    List<LoanTransaction> getAllLoanTransactions();
    List<LoanTransaction> getLoanTransaction(Loan loan);
    LoanTransaction loanTransactionEntry(LoanTransaction loanTransaction);
    LoanTransaction latestLoanTransaction(Loan loan);
}