package com.dfq.coeffi.cbs.report.accountReports;

import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface AccountReportRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE date(t.transactionOn) BETWEEN :dateFrom AND :dateTo")
    List<Transaction> getCashBookDetails(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT t FROM Transaction t WHERE date(t.transactionOn) BETWEEN :dateFrom AND :dateTo ")
    List<Transaction> getDayBookDetails(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT t FROM Transaction t WHERE (date(t.transactionOn) BETWEEN :dateFrom AND :dateTo) AND (t.particulars=:accountHeads)")
    List<Transaction> getGeneralLedgerDetails(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("accountHeads") String accountHeads);

    @Query("select t from Transaction t WHERE id in (select max(tr.id) from Transaction tr group by tr.accountNumber) AND (date(t.transactionOn) BETWEEN :dateFrom AND :dateTo)")
    List<Transaction> getTrialBalance(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT transaction FROM Transaction transaction WHERE (date(transaction.createdOn) BETWEEN :dateFrom AND :dateTo) AND transaction.ledger.name=:ledger")
    List<Transaction> getCashBookDetailsByLedger(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("ledger") String ledger);

    @Query("SELECT t FROM Transaction t WHERE date(t.transactionOn) BETWEEN :dateFrom AND :dateTo AND t.transferType=:transferType")
    List<Transaction> getDayBookDetailsByTransferType(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("transferType") String transferType);
}
