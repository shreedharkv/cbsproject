package com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction;

import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;

import java.util.List;

public interface PigmyDepositTransactionService {

    PigmyDepositTransaction createPigmyDepositTransaction(PigmyDepositTransaction pigmyDepositTransaction);
    List<PigmyDepositTransaction> getAllPigmyDepositTransactions(String accountNumber);
    PigmyDepositTransaction getLatestTransaction(String accountNumber);

    List<PigmyDepositTransaction> getPigmyDepositTransactions(PigmyDeposit pigmyDeposit);
}
