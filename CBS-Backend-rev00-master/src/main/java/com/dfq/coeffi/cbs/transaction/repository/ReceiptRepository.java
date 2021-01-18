package com.dfq.coeffi.cbs.transaction.repository;

import com.dfq.coeffi.cbs.transaction.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@Transactional
@EnableJpaRepositories
public interface ReceiptRepository extends JpaRepository<Receipt,Long> {

}
