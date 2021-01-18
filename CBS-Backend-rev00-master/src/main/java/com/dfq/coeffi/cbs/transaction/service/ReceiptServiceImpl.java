package com.dfq.coeffi.cbs.transaction.service;

import com.dfq.coeffi.cbs.transaction.entity.Receipt;
import com.dfq.coeffi.cbs.transaction.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiptServiceImpl implements ReceiptService {

    @Autowired
    public ReceiptRepository receiptRepository;


    @Override
    public Receipt createReceipt(Receipt receipt) {
        return receiptRepository.save(receipt);
    }

    @Override
    public List<Receipt> getAllReceipts() {
        return receiptRepository.findAll();
    }
}
