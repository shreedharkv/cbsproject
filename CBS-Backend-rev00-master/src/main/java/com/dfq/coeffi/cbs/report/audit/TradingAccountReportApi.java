package com.dfq.coeffi.cbs.report.audit;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
public class TradingAccountReportApi extends BaseController {


    private final AccountHeadService accountHeadService;
    private final TransactionService transactionService;

    @Autowired
    public TradingAccountReportApi(final AccountHeadService accountHeadService, final TransactionService transactionService) {
        this.accountHeadService = accountHeadService;
        this.transactionService = transactionService;
    }

    @GetMapping("/audit/trading-account")
    public ResponseEntity<TradingAccountDto> getCreditDebitAudit() {

        AccountHead accountHead = accountHeadService.findByName("Trading Account");
        List<Transaction> tradingAccountCreditTransactions = transactionService.getAllTransactions(accountHead, "CREDIT");
        List<Transaction> tradingAccountDebitTransactions = transactionService.getAllTransactions(accountHead, "DEBIT");
        TradingAccountDto dto = new TradingAccountDto();
        BigDecimal totalSales = new BigDecimal(BigInteger.ZERO);
        BigDecimal totalPurchase = new BigDecimal(BigInteger.ZERO);

        if (tradingAccountCreditTransactions != null && tradingAccountCreditTransactions.size() > 0) {
            for (Transaction transaction : tradingAccountCreditTransactions) {
                totalPurchase = totalPurchase.add(transaction.getCreditAmount());
            }

            dto.setPurchaseTransactions(tradingAccountCreditTransactions);
            dto.setTotalPurchase(totalPurchase);
        }

        if (tradingAccountDebitTransactions != null && tradingAccountDebitTransactions.size() > 0) {

            for (Transaction transaction : tradingAccountDebitTransactions) {
                totalSales = totalSales.add(transaction.getDebitAmount());
            }

            dto.setSaleTransactions(tradingAccountDebitTransactions);
            dto.setTotalSales(totalSales);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}