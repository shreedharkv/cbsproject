package com.dfq.coeffi.cbs.report.bankReport;


import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface BankReportRepository extends JpaRepository<BankMaster,Long> {

    @Query("SELECT bm FROM BankMaster bm WHERE (:bankCodeFrom is null OR :bankCodeTo is null OR bm.bankCode BETWEEN :bankCodeFrom AND :bankCodeTo )")
    List<BankMaster> getBankDetails(@Param("bankCodeFrom") String bankCodeFrom, @Param("bankCodeTo") String bankCodeTo);

    //List<BankMaster> getBankReconciliationReport(Date dateFrom,Date dateTo,String bankCode);
}
