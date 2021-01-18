package com.dfq.coeffi.cbs.report.audit;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.transaction.entity.BankTransaction;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class ProfitLossDto {

    private BigDecimal totalAmount;

    private Ledger ledger;

    private AccountHead accountHead;

    private List<Transaction> profitTransactions;

    private List<Transaction> lossTransactions;

    private List<BankTransaction> externalBankTransactions;


    private String ledgerName;

    private Date startDate;
    private Date endDate;
}