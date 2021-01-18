package com.dfq.coeffi.cbs.transaction.service;

import com.dfq.coeffi.cbs.transaction.entity.Receipt;

import java.util.List;

public interface ReceiptService {

    Receipt createReceipt(Receipt receipt);
    List<Receipt> getAllReceipts();
}
