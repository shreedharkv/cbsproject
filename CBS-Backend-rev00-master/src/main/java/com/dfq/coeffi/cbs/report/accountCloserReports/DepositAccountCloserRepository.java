package com.dfq.coeffi.cbs.report.accountCloserReports;


import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface DepositAccountCloserRepository extends JpaRepository<ChildrensDeposit,Long> {

    @Query("SELECT cd FROM ChildrensDeposit cd WHERE (cd.isWithDrawn = true AND year(cd.accountClosedOn) = :year)")
    List<ChildrensDeposit> getAllChildrensDepositAccountCloser(@Param("year") int year);

     @Query("SELECT cd FROM DoubleScheme cd WHERE (cd.isWithDrawn = true AND year(cd.accountClosedOn) = :year)")
     List<DoubleScheme> getAllDoubleSchemeAccountCloser(@Param("year") int year);

     @Query("SELECT cd FROM RecurringDeposit cd WHERE (cd.isWithDrawn = true AND year(cd.accountClosedOn) = :year)")
     List<RecurringDeposit> getAllRecurringDepositAccountCloser(@Param("year") int year);

     @Query("SELECT cd FROM TermDeposit cd WHERE (cd.isWithDrawn = true AND year(cd.accountClosedOn) = :year)")
     List<TermDeposit> getAllTermDepositAccountCloser(@Param("year") int year);

     @Query("SELECT cd FROM FixedDeposit cd WHERE (cd.isWithDrawn = true AND year(cd.accountClosedOn) = :year)")
     List<FixedDeposit> getAllFixedDepositAccountCloser(@Param("year") int year);

     @Query("SELECT cd FROM SavingsBankDeposit cd WHERE (cd.accountStatus ='CLOSED' AND year(cd.accountClosedOn) = :year)")
     List<SavingsBankDeposit> getAllSavingsBankAccountCloser(@Param("year") int year);

     @Query("SELECT cd FROM CurrentAccount cd WHERE (cd.accountStatus ='CLOSED' AND year(cd.accountClosedOn) = :year)")
     List<CurrentAccount> getAllCurrentAccountCloser(@Param("year") int year);

      @Query("SELECT loan FROM Loan loan WHERE (loan.loanStatus ='LOAN_CLOSED' AND year(loan.loanDetail.loanClosedOn) = :year AND loan.loanType =:loanType)")
      List<Loan> getAllLoanCloser(@Param("year") int year, @Param("loanType") LoanType loanType);
}
