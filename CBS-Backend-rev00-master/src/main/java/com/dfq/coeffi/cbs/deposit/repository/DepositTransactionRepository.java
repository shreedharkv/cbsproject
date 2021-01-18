package com.dfq.coeffi.cbs.deposit.repository;


import com.dfq.coeffi.cbs.deposit.entity.DepositTransaction;
import com.dfq.coeffi.cbs.deposit.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface DepositTransactionRepository extends JpaRepository<DepositTransaction,Long> {


    @Query("SELECT d FROM DepositTransaction d WHERE d.accountNumber=:accountNumber and d.transactionType = :transactionType")
    List<DepositTransaction> getDepositTransactionByAccountnumber(@Param("accountNumber") String accountNumber,
                                                                  @Param("transactionType") TransactionType transactionType);
}
