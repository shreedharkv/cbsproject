package com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction;

import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface PigmyDepositTransactionRepository extends JpaRepository<PigmyDepositTransaction,Long> {

    @Query("SELECT bm FROM PigmyDepositTransaction bm WHERE bm.pigmyDeposit.accountNumber = :accountNumber")
    List<PigmyDepositTransaction> getLatestTransaction(@Param("accountNumber") String accountNumber);

    List<PigmyDepositTransaction> findByPigmyDeposit(PigmyDeposit pigmyDeposit);

    List<PigmyDepositTransaction> findByAccountNumber(String accountNumber);
}
