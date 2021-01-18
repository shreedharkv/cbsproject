package com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction;

import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;

public interface CurrentAccountTransactionService {

    CurrentAccountTransaction createCurrentAccountTransaction(CurrentAccountTransaction currentAccountTransaction);
    CurrentAccountTransaction getLatestTransactionOfCurrentAccount(String accountNumber);

}
