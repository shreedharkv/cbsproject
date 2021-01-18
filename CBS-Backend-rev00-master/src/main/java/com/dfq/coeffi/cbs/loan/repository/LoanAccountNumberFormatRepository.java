package com.dfq.coeffi.cbs.loan.repository;


import com.dfq.coeffi.cbs.loan.entity.LoanAccountNumberFormat;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface LoanAccountNumberFormatRepository extends JpaRepository<LoanAccountNumberFormat, Long> {

    LoanAccountNumberFormat findByActive(boolean active);
}
