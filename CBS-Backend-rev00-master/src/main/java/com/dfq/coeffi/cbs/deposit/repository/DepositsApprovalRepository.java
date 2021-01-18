package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.DepositsApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@Transactional
@EnableJpaRepositories
public interface DepositsApprovalRepository extends JpaRepository<DepositsApproval,Long> {
}
