package com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction;

import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface SavingBankTransactionRepository extends JpaRepository<SavingBankTransaction,Long> {

    @Query("SELECT sbt FROM SavingBankTransaction sbt WHERE (sbt.savingsBankDeposit.accountNumber=:accountNumber) AND " +
            "(sbt.savingsBankDeposit.isApproved=true)")
    List<SavingBankTransaction> getLatestTransactionOfSB(@Param("accountNumber") String accountNumber);

    @Query("SELECT sbt FROM SavingBankTransaction sbt WHERE (sbt.accountNumber=:accountNumber) AND " +
            "(sbt.savingsBankDeposit.isApproved=true)")
    List<SavingBankTransaction> getLatestTransactionOfSBAccountNumber(@Param("accountNumber") String accountNumber);


    List<SavingBankTransaction> findBySavingsBankDeposit(SavingsBankDeposit savingsBankDeposit);

}
