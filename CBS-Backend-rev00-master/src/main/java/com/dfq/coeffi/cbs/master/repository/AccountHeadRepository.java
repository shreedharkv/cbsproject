package com.dfq.coeffi.cbs.master.repository;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType;
import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface AccountHeadRepository extends JpaRepository<AccountHead, Long> {

    List<AccountHead> findByActive(boolean active);
    List<AccountHead> findByAccountHeadTypeAndActive(AccountHeadType accountHeadType, boolean active);

    @Query("SELECT accountHead from account_heads accountHead WHERE (accountHead.name like %:name% AND accountHead.accountHeadType=:accountHeadType)")
    AccountHead getAccountHeadByName(@Param("name")String name,@Param("accountHeadType")AccountHeadType accountHeadType);

    AccountHead findByName(String name);
}