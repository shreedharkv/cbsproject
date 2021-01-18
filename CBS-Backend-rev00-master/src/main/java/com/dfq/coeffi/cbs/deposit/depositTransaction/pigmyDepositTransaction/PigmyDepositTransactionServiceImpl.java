package com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction;


import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

@Service
public class PigmyDepositTransactionServiceImpl implements PigmyDepositTransactionService {

    @Autowired
    private PigmyDepositTransactionRepository pigmyDepositTransactionRepository;

    @Override
    public PigmyDepositTransaction createPigmyDepositTransaction(PigmyDepositTransaction pigmyDepositTransaction) {
        return pigmyDepositTransactionRepository.save(pigmyDepositTransaction);
    }

    @Override
    public List<PigmyDepositTransaction> getAllPigmyDepositTransactions(String accountNumber) {
        return pigmyDepositTransactionRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public PigmyDepositTransaction getLatestTransaction(String accountNumber) {

        PigmyDepositTransaction pigmyDepositTransaction= null;
        List<PigmyDepositTransaction> transactions = pigmyDepositTransactionRepository.getLatestTransaction(accountNumber);
        if(transactions != null && transactions.size() > 0){
            Collections.reverse(transactions);
            pigmyDepositTransaction = transactions.get(0);
        }
        return pigmyDepositTransaction;
    }

    @Override
    public List<PigmyDepositTransaction> getPigmyDepositTransactions(PigmyDeposit pigmyDeposit) {
        return newArrayList(pigmyDepositTransactionRepository.findByPigmyDeposit(pigmyDeposit));
    }


}
