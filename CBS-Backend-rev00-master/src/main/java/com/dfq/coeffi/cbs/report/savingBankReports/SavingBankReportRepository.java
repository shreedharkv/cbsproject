package com.dfq.coeffi.cbs.report.savingBankReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface SavingBankReportRepository extends JpaRepository<SavingsBankDeposit,Long> {

    @Query("SELECT sb FROM SavingsBankDeposit sb " +
            "WHERE (:dateFrom is null OR :dateTo is null OR date(sb.createdOn) BETWEEN :dateFrom AND :dateTo ) OR " +
            "(:applicationNumberFrom is null or :applicationNumberTo is null or sb.applicationNumber BETWEEN :applicationNumberFrom AND :applicationNumberTo))")
    List<SavingsBankDeposit> getSavingBankApplicationDetails(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
                                                             @Param("applicationNumberFrom") String applicationNumberFrom, @Param("applicationNumberTo") String applicationNumberTo);

    @Query("SELECT sb FROM SavingsBankDeposit sb " +
            "WHERE (sb.accountNumber BETWEEN :accountNumberFrom AND :accountNumberTo )")
    List<SavingsBankDeposit> getSavingBankMemberDetails(@Param("accountNumberFrom") String accountNumberFrom, @Param("accountNumberTo") String accountNumberTo);

    @Query("SELECT sbt FROM SavingBankTransaction sbt " +
            "WHERE (( date(sbt.transactionOn) BETWEEN :dateFrom AND :dateTo ) AND (sbt.savingsBankDeposit.accountNumber =:accountNumber))")
    List<SavingBankTransaction> getSBLedgerDetails(@Param("accountNumber") String accountNumber, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);
}
