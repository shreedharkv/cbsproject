package com.dfq.coeffi.cbs.bank.repository;

import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankRepository extends JpaRepository<BankMaster, Long> {

    List<BankMaster> findByActive(Boolean active);

    @Query("select bank from BankMaster bank where bank.active=true")
    BankMaster getActiveBank();

    @Query("select bank from BankMaster bank where bank.id = :bankId AND bank.active=true")
    BankMaster getBankByStatus(@Param("bankId") long bankId);
}