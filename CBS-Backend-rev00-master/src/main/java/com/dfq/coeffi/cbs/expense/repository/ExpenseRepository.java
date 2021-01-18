package com.dfq.coeffi.cbs.expense.repository;

import com.dfq.coeffi.cbs.expense.entity.Expense;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT expense FROM Expense expense WHERE (date(expense.createdOn) BETWEEN :dateFrom AND :dateTo)")
    List<Expense> getExpenseBetweenDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT expense FROM Expense expense WHERE (date(expense.createdOn) BETWEEN :dateFrom AND :dateTo) AND expense.ledger.id =:ledgerId")
    List<Expense> findByLedgerAndDateBetween(@Param("ledgerId") long ledgerId, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);
}