package com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction;

import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

@Service
public class SavingBankTransactionServiceImpl implements SavingBankTransactionService {

    @Autowired
    private SavingBankTransactionRepository savingBankTransactionRepository;

    @Override
    public SavingBankTransaction createSavingBankTransaction(SavingBankTransaction savingBankTransaction) {
        return savingBankTransactionRepository.save(savingBankTransaction);
    }

    @Override
    public SavingBankTransaction getLatestTransactionOfSB(String accountNumber) {

        SavingBankTransaction savingBankTransaction= null;
        List<SavingBankTransaction> transactions = savingBankTransactionRepository.getLatestTransactionOfSB(accountNumber);
        if(transactions != null && transactions.size() > 0){
            Collections.reverse(transactions);
            savingBankTransaction = transactions.get(0);
        }
        return savingBankTransaction;
    }

    @Override
    public SavingBankTransaction getLatestTransactionOfSBAccountNumber(String accountNumber) {
        SavingBankTransaction savingBankTransaction= null;
        List<SavingBankTransaction> transactions = savingBankTransactionRepository.getLatestTransactionOfSBAccountNumber(accountNumber);
        if(transactions != null && transactions.size() > 0){
            Collections.reverse(transactions);
            savingBankTransaction = transactions.get(0);
        }
        return savingBankTransaction;
    }

    @Override
    public List<SavingBankTransaction> getSavingBankAccountTransactions(SavingsBankDeposit savingsBankDeposit) {
        return newArrayList(savingBankTransactionRepository.findBySavingsBankDeposit(savingsBankDeposit));
    }
}
