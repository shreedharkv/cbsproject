package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.ChildrensDeposit;
import com.dfq.coeffi.cbs.deposit.entity.DepositInterestCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
public interface DepositInterestCalculationRepository extends JpaRepository<DepositInterestCalculation,Long> {

    @Query("SELECT depositInterestCalculation FROM DepositInterestCalculation depositInterestCalculation WHERE " +
            "(date(depositInterestCalculation.createdOn) BETWEEN :dateFrom AND :dateTo AND depositInterestCalculation.accountNumber=:accountNumber)")
    List<DepositInterestCalculation> getDepositInterestCalculationBetweenDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("accountNumber") String accountNumber);

    List<DepositInterestCalculation> findByAccountNumber(String accountNumber);
}