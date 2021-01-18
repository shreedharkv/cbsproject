package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.DepositAccountNumberMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@Transactional
@EnableJpaRepositories
public interface DepositAccountNumberMasterRepository extends JpaRepository<DepositAccountNumberMaster,Long> {
}
