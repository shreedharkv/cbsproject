package com.dfq.coeffi.cbs.transaction.repository;

import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import com.dfq.coeffi.cbs.expense.entity.Expense;
import com.dfq.coeffi.cbs.transaction.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {

    List<BankTransaction> findByTransactionOn(Date transactionOn);

    @Query("SELECT bankTransaction FROM BankTransaction bankTransaction WHERE (date(bankTransaction.transactionOn) BETWEEN :dateFrom AND :dateTo)")
    List<BankTransaction> getBankTransactionBetweenDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT ca FROM CurrentAccountTransaction ca WHERE ca.currentAccount.accountNumber = :accountNumber)")
    List<BankTransaction> getLatestBankTransactions();
}