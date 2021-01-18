package com.dfq.coeffi.cbs.member.repository;

import com.dfq.coeffi.cbs.member.entity.DividendPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DividendPaymentRepository extends JpaRepository<DividendPayment, Long> {
}