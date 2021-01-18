package com.dfq.coeffi.cbs.master.repository;

import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import com.dfq.coeffi.cbs.master.entity.roi.loan.LoanRateOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface FinancialYearRepository extends JpaRepository<FinancialYear, Long> {

    FinancialYear findByActive(boolean active);
}