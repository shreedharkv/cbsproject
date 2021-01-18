package com.dfq.coeffi.cbs.deposit.depositTransaction.recurringDepositTransaction;

import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@EnableJpaRepositories
@Transactional
public interface RecurringDepositTransactionRepository extends JpaRepository<RecurringDepositTransaction,Long> {

    @Query("SELECT rdt FROM RecurringDepositTransaction rdt WHERE rdt.recurringDeposit.accountNumber = :accountNumber)")
    List<RecurringDepositTransaction> getLatestTransactionOfRecurringDepositTransaction(@Param("accountNumber") String accountNumber);
}
