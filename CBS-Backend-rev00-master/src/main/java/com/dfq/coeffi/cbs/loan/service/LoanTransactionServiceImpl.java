package com.dfq.coeffi.cbs.loan.service;

import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanTransaction;
import com.dfq.coeffi.cbs.loan.repository.LoanTransactionRepository;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class LoanTransactionServiceImpl implements LoanTransactionService {

    @Autowired
    private LoanTransactionRepository loanTransactionRepository;

    @Override
    public List<LoanTransaction> getAllLoanTransactions() {
        return loanTransactionRepository.findAll();
    }

    @Override
    public List<LoanTransaction> getLoanTransaction(Loan loan) {
        return loanTransactionRepository.findByLoan(loan);
    }

    @Override
    public LoanTransaction loanTransactionEntry(LoanTransaction loanTransaction) {
        return loanTransactionRepository.save(loanTransaction);
    }

    @Override
    public LoanTransaction latestLoanTransaction(Loan loan) {

        LoanTransaction latestLoanTransaction = null;
        List<LoanTransaction> transactions = getLoanTransaction(loan);
        if(transactions != null && transactions.size() > 0){
            Collections.reverse(transactions);
            latestLoanTransaction = transactions.get(0);
        }
        return latestLoanTransaction;
    }
}