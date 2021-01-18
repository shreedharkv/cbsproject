package com.dfq.coeffi.cbs.deposit.depositTransaction.recurringDepositTransaction;

import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RecurringDepositTransactionServiceImpl implements RecurringDepositTransactionService {

    @Autowired
    private RecurringDepositTransactionRepository recurringDepositTransactionRepository;


    @Override
    public RecurringDepositTransaction createRecurringDepositTransaction(RecurringDepositTransaction recurringDepositTransaction) {
        return recurringDepositTransactionRepository.save(recurringDepositTransaction);
    }

    @Override
    public RecurringDepositTransaction getLatestTransactionOfRecurringDepositTransaction(String accountNumber) {
        RecurringDepositTransaction recurringDepositTransaction= null;
        List<RecurringDepositTransaction> transactions = recurringDepositTransactionRepository.getLatestTransactionOfRecurringDepositTransaction(accountNumber);
        if(transactions != null && transactions.size() > 0){
            Collections.reverse(transactions);
            recurringDepositTransaction = transactions.get(0);
        }
        return recurringDepositTransaction;
    }

    @Override
    public List<RecurringDepositTransaction> getRecurringDepositTransactionsByAccountNumber(String accountNumber) {
        List<RecurringDepositTransaction> transactions = recurringDepositTransactionRepository.getLatestTransactionOfRecurringDepositTransaction(accountNumber);
        return transactions;
    }
}
