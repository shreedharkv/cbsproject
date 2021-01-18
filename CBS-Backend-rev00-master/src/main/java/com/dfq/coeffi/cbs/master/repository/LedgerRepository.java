package com.dfq.coeffi.cbs.master.repository;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    List<Ledger> findByActive(boolean active);
    List<Ledger> findByAccountHeadAndActive(AccountHead accountHead, boolean active);

    Ledger findByNameAndActive(String name, boolean active);
}