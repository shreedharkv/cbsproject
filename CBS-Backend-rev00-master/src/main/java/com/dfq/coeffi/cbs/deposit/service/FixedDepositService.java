package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FixedDepositService {
    FixedDeposit saveFixedDeposit(FixedDeposit fixedDeposit);
    List<FixedDeposit> getAllFixedDeposits(Date dateFrom, Date dateTo);
    List<FixedDeposit> getAllFixedDeposits(Date dateFrom, Date dateTo,boolean status);
    Optional<FixedDeposit> getFixedDepositById(long id);
    void deleteFixedDeposit(long id);
    List<FixedDeposit> getFixedDepositListByDate(Date dateFrom, Date dateTo);
    Optional<FixedDeposit> getFixedDepositByAccountNumber(String accountNumber);
    List<FixedDeposit> getFixedDepositByCustomerId(long customerId);
    List<FixedDeposit> getFixedDepositByMemberNumber(String memberNumber);
    void checkFixedDepositForLoan(String accountNumber);

    List<FixedDeposit> getActiveFixedDeposits();
}
