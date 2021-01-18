package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.entity.branch.Branch;

import java.util.List;

public interface AccountHeadService {

    AccountHead getAccountHead(long id);
    List<AccountHead> getAccountHeads();
    List<AccountHead> getAccountHeads(AccountHeadType accountHeadType);
    AccountHead createNewAccountHead(AccountHead accountHead);
    AccountHead getAccountHeadByName(String name,AccountHeadType accountHeadType);
    AccountHead findByName(String name);

    Ledger getLedger(long id);
    List<Ledger> getLedgers();
    List<Ledger> getAccountHeadLedgers(AccountHead accountHead);
    Ledger createNewLedger(Ledger ledger);

    Ledger getLedgerByName(String name);
}