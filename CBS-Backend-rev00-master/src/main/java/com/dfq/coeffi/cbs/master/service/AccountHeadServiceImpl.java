package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.repository.AccountHeadRepository;
import com.dfq.coeffi.cbs.master.repository.LedgerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class AccountHeadServiceImpl implements AccountHeadService {

    @Autowired
    private AccountHeadRepository accountHeadRepository;

    @Autowired
    private LedgerRepository ledgerRepository;

    @Override
    public AccountHead getAccountHead(long id) {
        return accountHeadRepository.findOne(id);
    }

    @Override
    public List<AccountHead> getAccountHeads() {
        return accountHeadRepository.findByActive(true);
    }

    @Override
    public List<AccountHead> getAccountHeads(AccountHeadType accountHeadType) {
        return accountHeadRepository.findByAccountHeadTypeAndActive(accountHeadType, true);
    }

    @Override
    public AccountHead createNewAccountHead(AccountHead accountHead) {
        return accountHeadRepository.save(accountHead);
    }

    @Override
    public AccountHead getAccountHeadByName(String name,AccountHeadType accountHeadType) {
        return accountHeadRepository.getAccountHeadByName(name,accountHeadType);
    }

    @Override
    public AccountHead findByName(String name) {
        return accountHeadRepository.findByName(name);
    }


    @Override
    public Ledger getLedger(long id) {
        return ledgerRepository.getOne(id);
    }

    @Override
    public List<Ledger> getLedgers() {
        return ledgerRepository.findByActive(true);
    }

    @Override
    public List<Ledger> getAccountHeadLedgers(AccountHead accountHead) {
        return ledgerRepository.findByAccountHeadAndActive(accountHead, true);
    }

    @Override
    public Ledger createNewLedger(Ledger ledger) {
        return ledgerRepository.save(ledger);
    }

    @Override
    public Ledger getLedgerByName(String name) {
        return ledgerRepository.findByNameAndActive(name, true);
    }

}