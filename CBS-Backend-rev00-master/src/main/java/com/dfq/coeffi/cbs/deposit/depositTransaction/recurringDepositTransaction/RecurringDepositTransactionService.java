package com.dfq.coeffi.cbs.deposit.depositTransaction.recurringDepositTransaction;

import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;

import java.util.List;

public interface RecurringDepositTransactionService {

    RecurringDepositTransaction createRecurringDepositTransaction(RecurringDepositTransaction recurringDepositTransaction);
    RecurringDepositTransaction getLatestTransactionOfRecurringDepositTransaction(String accountNumber);
    List<RecurringDepositTransaction> getRecurringDepositTransactionsByAccountNumber(String accountNumber);

}
