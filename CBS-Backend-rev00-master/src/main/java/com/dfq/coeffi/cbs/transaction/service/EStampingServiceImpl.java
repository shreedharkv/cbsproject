package com.dfq.coeffi.cbs.transaction.service;

import com.dfq.coeffi.cbs.transaction.entity.EStamping;
import com.dfq.coeffi.cbs.transaction.repository.EStampingRepository;
import com.dfq.coeffi.cbs.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class EStampingServiceImpl implements EStampingService {

    @Autowired
    private EStampingRepository eStampingRepository;

    @Override
    public List<EStamping> getAllEStampingTransactions() {
        return eStampingRepository.findAll();
    }

    @Override
    public EStamping eStampingEntry(EStamping eStamping) {
        return eStampingRepository.save(eStamping);
    }

    @Override
    public EStamping latestEStatmpingTransaction() {
        EStamping latestTransaction = null;
        List<EStamping> transactions = eStampingRepository.findAll();
        if (transactions != null && transactions.size() > 0) {
            Collections.reverse(transactions);
            latestTransaction = transactions.get(0);
        }else {
            latestTransaction.setTransactionOn(DateUtil.getTodayDate());
            latestTransaction.setTransactionType("CREDIT");
            latestTransaction.setCreditAmount(BigDecimal.ZERO);
            latestTransaction.setDebitAmount(BigDecimal.ZERO);
            latestTransaction.setBalance(BigDecimal.ZERO);
            latestTransaction = eStampingRepository.save(latestTransaction);
        }
        return latestTransaction;
    }
}
