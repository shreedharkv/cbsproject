package com.dfq.coeffi.cbs.loan.repository;

import com.dfq.coeffi.cbs.loan.entity.loan.LoanInstallments;
import org.springframework.data.jpa.repository.JpaRepository;
import javax.transaction.Transactional;

@Transactional
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallments, Long> {

}