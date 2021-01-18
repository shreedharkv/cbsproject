package com.dfq.coeffi.cbs.transaction.service;

import com.dfq.coeffi.cbs.transaction.entity.EStamping;
import java.util.List;

public interface EStampingService {

    List<EStamping> getAllEStampingTransactions();
    EStamping eStampingEntry(EStamping eStamping);
    EStamping latestEStatmpingTransaction();
}