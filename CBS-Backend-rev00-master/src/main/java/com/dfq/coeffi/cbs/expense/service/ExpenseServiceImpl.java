package com.dfq.coeffi.cbs.expense.service;

import com.dfq.coeffi.cbs.expense.entity.Expense;
import com.dfq.coeffi.cbs.expense.repository.ExpenseRepository;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService{

    @Autowired
    private ExpenseRepository expenseRepository;

    @Override
    public List<Expense> getExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public List<Expense> getExpenses(long ledgerId, Date fromDate, Date toDate) {
        return expenseRepository.findByLedgerAndDateBetween(ledgerId, fromDate, toDate);
    }

    @Override
    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public Optional<Expense> getExpense(long id) {
        return ofNullable(expenseRepository.findOne(id));
    }

    @Override
    public List<Expense> getExpenseBetweenDates(Date fromDate, Date toDate) {
        return expenseRepository.getExpenseBetweenDate(fromDate, toDate);
    }
}