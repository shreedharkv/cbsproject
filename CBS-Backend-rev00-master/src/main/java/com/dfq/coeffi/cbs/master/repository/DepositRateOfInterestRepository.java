package com.dfq.coeffi.cbs.master.repository;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.deposit.entity.DepositType;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.master.entity.roi.fd.DepositRateOfInterest;
import com.dfq.coeffi.cbs.master.entity.roi.loan.LoanRateOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface DepositRateOfInterestRepository extends JpaRepository<DepositRateOfInterest, Long> {

    DepositRateOfInterest findByDepositTypeAndActive(DepositType depositType, boolean active);

    List<DepositRateOfInterest> findByActive(Boolean status);
}