package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.TermDeposit;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TermDepositService {

    TermDeposit saveTermDeposit(TermDeposit termDeposit);
    List<TermDeposit> getAllTermDeposits(Date dateFrom, Date dateTo);
    Optional<TermDeposit> getTermDepositById(long id);
    void deleteTermDeposit(long id);
    Optional<TermDeposit> getTermDepositByAccountNumber(String accountNumber);
    List<TermDeposit> getTermDepositByMemberNumber(String memberNumber);
}