package com.dfq.coeffi.cbs.report.transactionReport;

import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

public interface TransactionReportRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT transaction FROM Transaction transaction WHERE (date(transaction.createdOn) BETWEEN :dateFrom AND :dateTo) AND transaction.accountNumber=:accountNumber")
    List<Transaction> getTransactionByAccountNumberAndBetweenDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("accountNumber") String accountNumber);

    @Query("SELECT transaction FROM Transaction transaction WHERE (date(transaction.createdOn) BETWEEN :dateFrom AND :dateTo) AND transaction.accountHead.name=:accountHead")
    List<Transaction> getTransactionByAccountHeadAndBetweenDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("accountHead") String accountHead);

    @Query("SELECT transaction FROM Transaction transaction WHERE (date(transaction.createdOn) BETWEEN :dateFrom AND :dateTo) AND transaction.accountHead.name=:accountHead AND transaction.ledger.name=:ledger")
    List<Transaction> getTransactionByLedgerAndBetweenDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("accountHead") String accountHead, @Param("ledger") String ledger);

    @Query("SELECT transaction FROM Transaction transaction WHERE (date(transaction.createdOn) BETWEEN :dateFrom AND :dateTo)")
    List<Transaction> getTransactionByBetweenDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT transaction FROM Transaction transaction WHERE (date(transaction.createdOn) BETWEEN :dateFrom AND :dateTo) AND transaction.ledger.name=:ledger AND transaction.accountNumber=:accountNumber")
    List<Transaction> getTransactionByDateAndLedgerAndAccountNumber(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("ledger") String ledger, @Param("accountNumber") String accountNumber);
}