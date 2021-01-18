package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.ChildrensDeposit;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ChildrensDepositService {
    ChildrensDeposit saveDeposit(ChildrensDeposit deposit);
    List<ChildrensDeposit> getAllDeposits(Date dateFrom, Date dateTo);
    Optional<ChildrensDeposit> getDepositById(long id);
    void deleteDeposit(long id);
    List<ChildrensDeposit> getAllApprovedDeposits();
    Optional<ChildrensDeposit> getChildrenDepositByAccountNumber(String accountNumber);
    List<ChildrensDeposit> getChildrenDepositByMemberNumber(String memberNumber);
}