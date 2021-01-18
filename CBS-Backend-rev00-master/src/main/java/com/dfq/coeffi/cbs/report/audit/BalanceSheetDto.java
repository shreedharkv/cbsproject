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
public class BalanceSheetDto {

    private BigDecimal totalAmount;

    private Ledger ledger;

    private AccountHead accountHead;

    private List<Transaction> AssetTransactions;

    private List<Transaction> liabilityTransactions;

    private List<BankTransaction> externalBankTransactions;

    private Date startDate;
    private Date endDate;
}