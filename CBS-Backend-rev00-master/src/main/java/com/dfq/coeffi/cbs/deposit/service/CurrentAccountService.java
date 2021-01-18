package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.CurrentAccount;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import com.dfq.coeffi.cbs.member.entity.Member;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CurrentAccountService {
    CurrentAccount saveCurrentAccount(CurrentAccount currentAccount);
    List<CurrentAccount> getAllCurrentAccountDeposits(Date dateFrom, Date dateTo);
    Optional<CurrentAccount> getCurrentAccountDepositById(long id);
    void deleteCurrentAccountDeposit(long id);
    List<CurrentAccount> getAllApprovedCurrentAccountDeposits();

    List<CurrentAccount> getAllCurrentAccountsByMember(Member member);
    Optional<CurrentAccount> getCurrentAccountDepositByAccountNumber(String accountNumber);
}