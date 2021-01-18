package com.dfq.coeffi.cbs.loan.repository;

import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanTransaction;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.hibernate.validator.constraints.EAN;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long> {

    List<LoanTransaction> findByLoan(Loan loan);
}
