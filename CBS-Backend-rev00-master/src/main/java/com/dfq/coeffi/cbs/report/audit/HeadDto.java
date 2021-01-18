package com.dfq.coeffi.cbs.report.audit;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class HeadDto {

    private BigDecimal amount;

    private AccountHead accountHead;

    private String transactionType;

    private List<Transaction> ledgerTransactions;


    private String narration;

    private Ledger ledger;
}
