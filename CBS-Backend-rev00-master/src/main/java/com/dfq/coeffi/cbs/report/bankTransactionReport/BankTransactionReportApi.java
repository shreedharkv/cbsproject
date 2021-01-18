package com.dfq.coeffi.cbs.report.bankTransactionReport;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.transaction.entity.BankTransaction;
import com.dfq.coeffi.cbs.transaction.service.BankTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@Slf4j
public class BankTransactionReportApi extends BaseController {

    private final BankTransactionService bankTransactionService;

    @Autowired
    private BankTransactionReportApi(final BankTransactionService bankTransactionService){
        this.bankTransactionService = bankTransactionService;
    }

    @PostMapping("/bank-transaction/report-by-date")
    public ResponseEntity<List<BankTransaction>> getBankTransactionsByDate(@RequestBody BankTransactionReportDto bankTransactionReportDto) {
        List<BankTransaction> bankTransactions = bankTransactionService.getBankTransactionsByBetweenDates(bankTransactionReportDto.getDateFrom(), bankTransactionReportDto.getDateTo());
        if (CollectionUtils.isEmpty(bankTransactions)) {
            log.warn("No bank trnsactions found");
            throw new EntityNotFoundException("bankTransactions");
        }
        return new ResponseEntity<>(bankTransactions, HttpStatus.OK);
    }
}
