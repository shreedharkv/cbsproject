package com.dfq.coeffi.cbs.loan.repository;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.loan.entity.LoanStatus;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface GoldLoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByActive(boolean active);
    List<Loan> findByLoanTypeAndActive(LoanType loanType, boolean active);
    Loan findByApplicationNumber(long applicationNumber);
    Loan findByLoanAccountNumber(String loanAccountNumber);

    List<Loan> findByMember(Member member);
    Loan findFirstByOrderByIdDesc();

    @Query("SELECT loan FROM Loan loan WHERE loan.loanDetail.customer.id = :cstId AND loan.loanStatus= :loanStatus")
    List<Loan> findLoanByCustomerId(@Param("cstId") long cstId,@Param("loanStatus") LoanStatus loanStatus);

    @Query("SELECT loan FROM Loan loan WHERE loan.loanDetail.ladDetails.fixedDeposit.member.customer.id = :cstId AND loan.loanStatus= :loanStatus")
    List<Loan> getFixedDepositLoans(@Param("cstId") long cstId,@Param("loanStatus") LoanStatus loanStatus);

    @Query("SELECT loan FROM Loan loan WHERE loan.loanDetail.termDetails.member.customer.id = :cstId AND loan.loanStatus= :loanStatus")
    List<Loan> getMemberTermLoans(@Param("cstId") long cstId,@Param("loanStatus") LoanStatus loanStatus);

    @Query("SELECT loan FROM Loan loan WHERE loan.loanDetail.customer.id = :cstId")
    List<Loan> findLoanByCustomerId(@Param("cstId") long cstId);

    @Query("SELECT loan FROM Loan loan WHERE loan.loanDetail.ladDetails.fixedDeposit.member.customer.id = :cstId")
    List<Loan> getFixedDepositLoans(@Param("cstId") long cstId);

    @Query("SELECT loan FROM Loan loan WHERE loan.loanDetail.termDetails.member.customer.id = :cstId")
    List<Loan> getMemberTermLoans(@Param("cstId") long cstId);

    @Query("SELECT loan FROM Loan loan WHERE loan.loanDetail.approved =:status")
    List<Loan> getUnApprovedLoans(@Param("status")boolean status);

    @Query("SELECT loan FROM Loan loan WHERE loan.loanDetail.ladDetails.fixedDeposit.accountNumber = :accountNumber AND loan.loanStatus= :loanStatus")
    List<Loan> goldLoanRepository(@Param("accountNumber")String accountNumber,@Param("loanStatus") LoanStatus loanStatus);

    @Query("SELECT loan FROM Loan loan WHERE loan.loanDetail.ladDetails.recurringDeposit.accountNumber = :accountNumber AND loan.loanStatus= :loanStatus")
    List<Loan> getLoanAgainstRecuringDeposit(@Param("accountNumber")String accountNumber,@Param("loanStatus") LoanStatus loanStatus);

    @Query("SELECT loan FROM Loan loan WHERE loan.loanDetail.ladDetails.pigmyDeposit.accountNumber = :accountNumber AND loan.loanStatus= :loanStatus")
    List<Loan> getLoanAgainstPigmyDeposit(@Param("accountNumber")String accountNumber,@Param("loanStatus") LoanStatus loanStatus);
}