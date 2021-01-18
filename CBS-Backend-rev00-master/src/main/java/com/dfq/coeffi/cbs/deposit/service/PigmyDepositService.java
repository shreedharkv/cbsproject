package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.ChildrensDeposit;
import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import com.dfq.coeffi.cbs.member.entity.Member;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PigmyDepositService {

    PigmyDeposit createPigmyDeposit(PigmyDeposit pigmyDeposit);
    List<PigmyDeposit> getAllPigmyDeposits(Date dateFrom, Date dateTo);
    List<PigmyDeposit> getAllPigmyDeposits(Date dateFrom, Date dateTo,boolean status);
    Optional<PigmyDeposit> getPigmyDepositById(long id);
    Optional<PigmyDeposit> getPigmyDepositByAccountNumber(String accountNumber);
    List<PigmyDeposit> getPigmyDepositByCustomerId(long customerId);

    List<PigmyDeposit> getAllPigmyDepositByMember(Member member);
    List<PigmyDeposit> getPigmyDepositActiveAccounts();

    List<PigmyDeposit> getPigmyDepositByMemberNumber(String memberNumber);

    Optional<PigmyDeposit> getPigmyDepositByAccountNumberByStatus(String accountNumber);
    void checkPigmyDepositForLoan(String accountNumber);
}