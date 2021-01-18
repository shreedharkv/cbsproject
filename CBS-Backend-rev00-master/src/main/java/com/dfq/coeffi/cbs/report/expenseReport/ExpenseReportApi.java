package com.dfq.coeffi.cbs.report.expenseReport;

import com.dfq.coeffi.cbs.expense.entity.Expense;
import com.dfq.coeffi.cbs.expense.service.ExpenseService;
import com.dfq.coeffi.cbs.init.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RestController
public class ExpenseReportApi extends BaseController {

    private final ExpenseService expenseService;

    @Autowired
    private ExpenseReportApi(final ExpenseService expenseService){
        this.expenseService = expenseService;
    }

    @PostMapping("/expense/report-by-date")
    public ResponseEntity<List<Expense>> getExpenses(@RequestBody ExpenseDto expenseDto) {
        List<Expense> expenses = expenseService.getExpenseBetweenDates(expenseDto.getDateFrom(), expenseDto.getDateTo());
        if (CollectionUtils.isEmpty(expenses)) {
            log.warn("No expenses found");
            throw new EntityNotFoundException("expenses");
        }
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }
}