package com.dfq.coeffi.cbs.report.loanReports;

import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface LoanReportRepository extends JpaRepository<Loan,Long> {

    @Query("SELECT loan FROM Loan loan " +
            "WHERE (:loanAccountNumberFrom is null or :loanAccountNumberTo is null or loan.loanAccountNumber BETWEEN :loanAccountNumberFrom AND :loanAccountNumberTo) OR " +
            "(:exgLoanAccountNumberFrom is null or :exgLoanAccountNumberTo is null or loan.exgLoanAccountNumber BETWEEN :exgLoanAccountNumberFrom AND :exgLoanAccountNumberTo)")
    List<Loan> getGoldLoanRecoveryDetailsByAccountNumber(@Param("loanAccountNumberFrom") String loanAccountNumberFrom, @Param("loanAccountNumberTo") String loanAccountNumberTo, @Param("exgLoanAccountNumberFrom") String exgLoanAccountNumberFrom, @Param("exgLoanAccountNumberTo") String exgLoanAccountNumberTo);

    @Query("SELECT loan FROM Loan loan " +
            "WHERE (:loanAccountNumberFrom is null or :loanAccountNumberTo is null or loan.loanAccountNumber BETWEEN :loanAccountNumberFrom AND :loanAccountNumberTo) OR " +
            "(:exgLoanAccountNumberFrom is null or :exgLoanAccountNumberTo is null or loan.exgLoanAccountNumber BETWEEN :exgLoanAccountNumberFrom AND :exgLoanAccountNumberTo)")
    List<Loan> getGoldLoanRegisterDetailsByAccountNumber(@Param("loanAccountNumberFrom") String loanAccountNumberFrom, @Param("loanAccountNumberTo") String loanAccountNumberTo, @Param("exgLoanAccountNumberFrom") String exgLoanAccountNumberFrom, @Param("exgLoanAccountNumberTo") String exgLoanAccountNumberTo);


    List<Loan> findByLoanAccountNumberBetween(String loanAccountNumberFrom, String loanAccountNumberTo);
    List<Loan> findByExgLoanAccountNumberBetween(String loanAccountNumberFrom, String loanAccountNumberTo);



}