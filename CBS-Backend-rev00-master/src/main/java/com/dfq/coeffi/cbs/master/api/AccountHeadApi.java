package com.dfq.coeffi.cbs.master.api;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.master.service.BranchService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.EAN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
public class AccountHeadApi extends BaseController {

    @Autowired
    private AccountHeadService accountHeadService;

    @GetMapping("account-heads")
    public ResponseEntity<List<AccountHead>> getActiveAccountHeads() {
        List<AccountHead> accountHeads = accountHeadService.getAccountHeads();
        if (CollectionUtils.isEmpty(accountHeads)) {
            throw new EntityNotFoundException("No active account heads found");
        }
        return new ResponseEntity<>(accountHeads, HttpStatus.OK);
    }

    @GetMapping("account-credit-heads")
    public ResponseEntity<List<AccountHead>> getCreditAccountHeads() {
        List<AccountHead> accountHeads = accountHeadService.getAccountHeads(AccountHeadType.CREDIT);
        if (CollectionUtils.isEmpty(accountHeads)) {
            throw new EntityNotFoundException("No active account heads found");
        }
        return new ResponseEntity<>(accountHeads, HttpStatus.OK);
    }

    @GetMapping("account-debit-heads")
    public ResponseEntity<List<AccountHead>> getDebitAccountHeads() {
        List<AccountHead> accountHeads = accountHeadService.getAccountHeads(AccountHeadType.DEBIT);
        if (CollectionUtils.isEmpty(accountHeads)) {
            throw new EntityNotFoundException("No active account heads found");
        }
        return new ResponseEntity<>(accountHeads, HttpStatus.OK);
    }

    @PostMapping("account-heads")
    public ResponseEntity<AccountHead> createNewAccountHead(@RequestBody  final AccountHead accountHead) {
        accountHead.setActive(true);
        AccountHead persistedObject = accountHeadService.createNewAccountHead(accountHead);
        if (persistedObject != null) log.info("New account head created");
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @GetMapping("ledger")
    public ResponseEntity<List<Ledger>> getActiveLedgers() {
        List<Ledger> ledgers = accountHeadService.getLedgers();
        if (CollectionUtils.isEmpty(ledgers)) {
            throw new EntityNotFoundException("No active ledgers found");
        }
        return new ResponseEntity<>(ledgers, HttpStatus.OK);
    }

    @GetMapping("account-head-ledger/{accountHeadId}")
    public ResponseEntity<List<Ledger>> getActiveLedgers(@PathVariable("accountHeadId") long accountHeadId) {
        List<Ledger> ledgers = null;
        AccountHead accountHead = accountHeadService.getAccountHead(accountHeadId);
        if(accountHead !=  null){
            ledgers = accountHeadService.getAccountHeadLedgers(accountHead);
            if (CollectionUtils.isEmpty(ledgers)) {
                throw new EntityNotFoundException("No active ledgers found");
            }
        }
        return new ResponseEntity<>(ledgers, HttpStatus.OK);
    }

    @PostMapping("ledger/{accountHeadId}")
    public ResponseEntity<Ledger> createNewAccountHead(@PathVariable("accountHeadId") long accountHeadId, @RequestBody  final Ledger ledger) {
        Ledger persistedObject = null;
        AccountHead accountHead = accountHeadService.getAccountHead(accountHeadId);
        if(accountHead !=  null){
            ledger.setAccountHead(accountHead);
            ledger.setActive(true);
            persistedObject = accountHeadService.createNewLedger(ledger);
        }
        if (persistedObject != null) log.info("New ledger created");
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @GetMapping("ledger/{name}")
    public ResponseEntity<Ledger> getLedgerByName(@PathVariable String name) {
        Ledger ledger = accountHeadService.getLedgerByName(name);
        return new ResponseEntity<>(ledger, HttpStatus.OK);
    }
}