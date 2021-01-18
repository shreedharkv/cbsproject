package com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction;

import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;

import java.util.List;

public interface SavingBankTransactionService {
    SavingBankTransaction createSavingBankTransaction(SavingBankTransaction savingBankTransaction);
    SavingBankTransaction getLatestTransactionOfSB(String accountNumber);
    SavingBankTransaction getLatestTransactionOfSBAccountNumber(String accountNumber);

    List<SavingBankTransaction> getSavingBankAccountTransactions(SavingsBankDeposit savingsBankDeposit);
}
