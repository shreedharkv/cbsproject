package com.dfq.coeffi.cbs.master.repository;

import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Branch findByActive(boolean active);
}