package com.dfq.coeffi.cbs.report.audit;

import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class TradingAccountDto {

    private BigDecimal totalSales;
    private BigDecimal totalPurchase;
    private List<Transaction> purchaseTransactions;
    private List<Transaction> saleTransactions;
}