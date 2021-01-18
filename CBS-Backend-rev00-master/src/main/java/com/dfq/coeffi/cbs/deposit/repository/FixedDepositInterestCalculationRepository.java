package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.DepositInterestCalculation;
import com.dfq.coeffi.cbs.deposit.entity.FixedDepositInterestCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
public interface FixedDepositInterestCalculationRepository extends JpaRepository<FixedDepositInterestCalculation, Long> {

    @Query("SELECT fixedDepositInterestCalculation FROM FixedDepositInterestCalculation fixedDepositInterestCalculation WHERE " +
            "(date(fixedDepositInterestCalculation.createdOn) BETWEEN :dateFrom AND :dateTo AND fixedDepositInterestCalculation.accountNumber=:accountNumber)")
    List<FixedDepositInterestCalculation> getFixedDepositInterestCalculationBetweenDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("accountNumber") String accountNumber);
}
