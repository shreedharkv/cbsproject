package com.dfq.coeffi.cbs.master.repository;

import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.master.entity.roi.loan.LoanRateOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface LoanRateOfInterestRepository extends JpaRepository<LoanRateOfInterest, Long> {

    LoanRateOfInterest findByLoanTypeAndActive(LoanType loanType, boolean active);
}