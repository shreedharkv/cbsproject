package com.dfq.coeffi.cbs.transaction.repository;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByTransactionOn(Date transactionOn);

    List<Transaction> findByTransferType(String transferType);

//    List<Transaction> findAllByOrderByIdDesc();

    @Query("SELECT t FROM Transaction t WHERE date(t.transactionOn) BETWEEN :dateFrom AND :dateTo AND t.transferType=:transferType")
    List<Transaction> getTransactionByDateAndTransferType(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("transferType") String transferType);

    @Query("SELECT t FROM Transaction t WHERE date(t.transactionOn) BETWEEN :dateFrom AND :dateTo")
    List<Transaction> getTransactionByBetweenDates(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    List<Transaction> findByLedger(Ledger ledger);

    List<Transaction> findByAccountHead(AccountHead accountHead);

    List<Transaction> findByAccountHeadAndTransactionType(AccountHead accountHead, String transactionType);
    List<Transaction> findByLedgerAndTransactionType(Ledger ledger, String transactionType);

    @Query("SELECT t FROM Transaction t WHERE date(t.transactionOn) BETWEEN :startDate AND :endDate AND t.ledger=:ledger")
    List<Transaction> getAllTransactions(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("ledger") Ledger ledger);
}