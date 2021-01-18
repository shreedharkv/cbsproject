package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.deposit.entity.DepositTransaction;
import com.dfq.coeffi.cbs.deposit.entity.TransactionType;
import com.dfq.coeffi.cbs.deposit.repository.DepositTransactionRepository;
import com.dfq.coeffi.cbs.deposit.service.DepositTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepositTransactionServiceImpl implements DepositTransactionService {

    private final DepositTransactionRepository depositTransactionRepository;

    @Autowired
    public DepositTransactionServiceImpl(DepositTransactionRepository depositTransactionRepository){
        this.depositTransactionRepository = depositTransactionRepository;
    }

    @Override
    public DepositTransaction createDepositTransactionService(DepositTransaction depositTransaction) {
        return depositTransactionRepository.save(depositTransaction);
    }

    @Override
    public List<DepositTransaction> getDepositTransactionByAccountnumber(String accountNumber,TransactionType transactionType) {
        return depositTransactionRepository.getDepositTransactionByAccountnumber(accountNumber,transactionType);
    }
}
