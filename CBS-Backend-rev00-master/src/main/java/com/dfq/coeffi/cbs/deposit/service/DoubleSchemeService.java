package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.DoubleScheme;
import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DoubleSchemeService {
    DoubleScheme saveDoubleSchemeDeposit(DoubleScheme doubleScheme);
    List<DoubleScheme> getAllDoubleSchemeDeposits(Date dateFrom, Date dateTo);
    Optional<DoubleScheme> getDoubleSchemeDepositById(long id);
    void deleteDoubleSchemeDeposit(long id);
    Optional<DoubleScheme> getDoubleSchemeByAccountNumber(String accountNumber);

    List<DoubleScheme> getDoubleSchemeDepositByMemberNumber(String memberNumber);
}
