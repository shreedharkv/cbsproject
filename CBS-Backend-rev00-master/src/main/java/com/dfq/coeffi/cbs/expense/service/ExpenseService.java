package com.dfq.coeffi.cbs.expense.service;

import com.dfq.coeffi.cbs.expense.entity.Expense;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ExpenseService {

    List<Expense> getExpenses();
    List<Expense> getExpenses(long ledgerId, Date fromDate, Date toDate);

    Expense saveExpense(Expense expense);

    Optional<Expense> getExpense(long id);

    List<Expense> getExpenseBetweenDates(Date fromDate, Date toDate);
}