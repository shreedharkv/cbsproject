package com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface CurrentAccountTransactionRepository extends JpaRepository<CurrentAccountTransaction,Long> {

    @Query("SELECT ca FROM CurrentAccountTransaction ca WHERE ca.currentAccount.accountNumber = :accountNumber)")
    List<CurrentAccountTransaction> getLatestTransactionOfCurrentAccount(@Param("accountNumber") String accountNumber);
}