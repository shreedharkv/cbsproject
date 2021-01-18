package com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction;

import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CurrentAccountTransactionServiceImpl implements CurrentAccountTransactionService {

    @Autowired
    private CurrentAccountTransactionRepository currentAccountTransactionRepository;
    @Override
    public CurrentAccountTransaction createCurrentAccountTransaction(CurrentAccountTransaction currentAccountTransaction) {
        return currentAccountTransactionRepository.save(currentAccountTransaction);
    }

    @Override
    public CurrentAccountTransaction getLatestTransactionOfCurrentAccount(String accountNumber) {
        CurrentAccountTransaction currentAccountTransaction= null;
        List<CurrentAccountTransaction> transactions = currentAccountTransactionRepository.getLatestTransactionOfCurrentAccount(accountNumber);
        if(transactions != null && transactions.size() > 0){
            Collections.reverse(transactions);
            currentAccountTransaction = transactions.get(0);
        }
        return currentAccountTransaction;
    }
}
