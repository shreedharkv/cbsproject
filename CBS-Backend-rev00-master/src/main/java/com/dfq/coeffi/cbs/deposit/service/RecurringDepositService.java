package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.DoubleScheme;
import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;
import com.dfq.coeffi.cbs.deposit.entity.RecurringDeposit;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RecurringDepositService {
    RecurringDeposit saveRecurringDeposit(RecurringDeposit recurringDeposit);
    List<RecurringDeposit> getAllRecurringDeposit(Date dateFrom, Date dateTo);
    List<RecurringDeposit> getAllRecurringDeposit(Date dateFrom, Date dateTo,boolean status);
    Optional<RecurringDeposit> getRecurringDepositById(long id);
    void deleteRecurringDeposit(long id);
    Optional<RecurringDeposit> getRecurringDepositByAccountNumber(String accountNumber);
    List<RecurringDeposit> getRecurringDepositByCustomerId(long customerId);

    List<RecurringDeposit> getRecuringDepositByMemberNumber(String memberNumber);
    void checkRecurringDepositForLoan(String accountNumber);
    List<RecurringDeposit> getAllRecurringDeposit();

}
