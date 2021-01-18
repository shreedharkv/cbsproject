package com.dfq.coeffi.cbs.member.repository;

import com.dfq.coeffi.cbs.member.entity.NumberFormat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NumberFormatRepository extends JpaRepository<NumberFormat, Long> {

    NumberFormat findByType(String type);
}