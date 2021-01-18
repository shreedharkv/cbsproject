package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.DepositTransaction;
import com.dfq.coeffi.cbs.deposit.entity.TransactionType;

import java.util.List;

public interface DepositTransactionService {

    DepositTransaction createDepositTransactionService(DepositTransaction depositTransaction);
    List<DepositTransaction> getDepositTransactionByAccountnumber(String accountNumber, TransactionType transactionType);

}
