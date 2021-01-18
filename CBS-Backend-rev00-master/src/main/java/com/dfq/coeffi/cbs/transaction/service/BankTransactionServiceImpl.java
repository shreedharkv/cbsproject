package com.dfq.coeffi.cbs.transaction.service;

import com.dfq.coeffi.cbs.transaction.entity.BankTransaction;
import com.dfq.coeffi.cbs.transaction.repository.BankTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class BankTransactionServiceImpl implements BankTransactionService{

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Override
    public List<BankTransaction> getAllBankTransactions() {
        return bankTransactionRepository.findAll();
    }

    @Override
    public BankTransaction bankTransactionEntry(BankTransaction bankTransaction) {
        return bankTransactionRepository.save(bankTransaction);
    }

    @Override
    public BankTransaction latestBankTransaction() {
        BankTransaction latestBankTransaction = null;
        List<BankTransaction> bankTransactions = bankTransactionRepository.findAll();
        if(bankTransactions != null && bankTransactions.size() > 0){
            Collections.reverse(bankTransactions);
            latestBankTransaction = bankTransactions.get(0);
        }
        return latestBankTransaction;
    }

    @Override
    public List<BankTransaction> getAllBankTransactions(Date fromDate, Date toDate) {
        return bankTransactionRepository.findAll();
    }

    @Override
    public List<BankTransaction> getAllBankTransactions(Date transactionDate) {
        return bankTransactionRepository.findByTransactionOn(transactionDate);
    }

    @Override
    public List<BankTransaction> getBankTransactionsByBetweenDates(Date dateFrom, Date dateTo) {
        return bankTransactionRepository.getBankTransactionBetweenDate(dateFrom, dateTo);
    }
}