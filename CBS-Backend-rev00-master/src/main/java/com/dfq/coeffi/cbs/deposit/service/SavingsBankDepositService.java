package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import com.dfq.coeffi.cbs.member.entity.Member;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SavingsBankDepositService {
    SavingsBankDeposit saveSavingsBankDeposit(SavingsBankDeposit savingsBankDeposit);
    List<SavingsBankDeposit> getAllSavingsBankDeposit(Date dateFrom, Date dateTo);
    Optional<SavingsBankDeposit> getSavingsBankDepositById(long id);
    void deleteSavingsBankDeposit(long id);
    SavingsBankDeposit getSavingsBankDepositByAccountNumber(String accountNumber);

    SavingsBankDeposit getSavingsBankAccountsByMember(Member member);

    List<SavingsBankDeposit> getAllSavingsBankAccountsByMember(Member member);
    void checkSavingBankAccount(Member member);

    SavingsBankDeposit getSavingsBankAccountByMemberNumber(String memberNumber);

    void checkLoanForMember(Member member);
    void checkDepositForMember(Member member);

    SavingsBankDeposit getAllSavingsBankAccountsByMemberId(Member member);

    List<SavingsBankDeposit> getActiveSavingBankDeposit();
}
