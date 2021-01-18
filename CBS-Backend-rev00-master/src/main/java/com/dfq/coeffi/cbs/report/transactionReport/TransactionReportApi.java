package com.dfq.coeffi.cbs.report.transactionReport;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
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
public class TransactionReportApi extends BaseController {

    private final TransactionReportService transactionReportService;

    @Autowired
    private TransactionReportApi(final TransactionReportService transactionReportService){
        this.transactionReportService = transactionReportService;
    }

    @PostMapping("transaction/account-number")
    public ResponseEntity<Transaction> getTransactionByAccountNumber(@RequestBody TransactionReportDto transactionReportDto){

        List<Transaction> transactionList = transactionReportService.getTransactionByAccountNumberAndBetweenDate(transactionReportDto.getDateFrom(), transactionReportDto.getDateTo(), transactionReportDto.getAccountNumber());
        if (CollectionUtils.isEmpty(transactionList)) {
            throw new EntityNotFoundException("No Transaction found for this " +transactionReportDto.getAccountNumber());
        }
        return new ResponseEntity(transactionList, HttpStatus.OK);
    }

    @PostMapping("transaction/account-head")
    public ResponseEntity<Transaction> getTransactionByAccountHead(@RequestBody TransactionReportDto transactionReportDto){

        List<Transaction> transactionList = transactionReportService.getTransactionByAccountHeadAndBetweenDate(transactionReportDto.getDateFrom(), transactionReportDto.getDateTo(), transactionReportDto.getAccountHead());
        if (CollectionUtils.isEmpty(transactionList)) {
            throw new EntityNotFoundException("No Transaction found for this " +transactionReportDto.getAccountNumber());
        }
        return new ResponseEntity(transactionList, HttpStatus.OK);
    }

    @PostMapping("transaction/account-ledger")
    public ResponseEntity<Transaction> getTransactionByAccountHeadAndLedger(@RequestBody TransactionReportDto transactionReportDto){

        List<Transaction> transactionList = transactionReportService.getTransactionByLedgerAndBetweenDate(transactionReportDto.getDateFrom(), transactionReportDto.getDateTo(), transactionReportDto.getAccountHead(), transactionReportDto.getLedger());
        if (CollectionUtils.isEmpty(transactionList)) {
            throw new EntityNotFoundException("No Transaction found for this " +transactionReportDto.getAccountNumber());
        }
        return new ResponseEntity(transactionList, HttpStatus.OK);
    }
}