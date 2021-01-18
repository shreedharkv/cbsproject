package com.dfq.coeffi.cbs.report.bankReport;


import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.report.PDFExcelFunction;
import com.dfq.coeffi.cbs.report.transactionReport.TransactionReportService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Transactional
@RestController
public class BankReportApi extends BaseController {

    private final BankReportService bankReportService;
    private final TransactionReportService transactionReportService;

    @Autowired
    private PDFExcelFunction pdfExcelFunction;

    @Autowired
    public BankReportApi(BankReportService bankReportService, final TransactionReportService transactionReportService) {
        this.bankReportService = bankReportService;
        this.transactionReportService = transactionReportService;
    }

    @PostMapping("report/bank/bank-details")
    public ResponseEntity<InputStreamResource> getBankDetailsByCode(@RequestBody BankReportDto bankReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {
        List<BankMaster> bankMasters = null;
        if (bankReportDto.reportType.equalsIgnoreCase("list")) {
            bankMasters = bankReportService.getBankDetails(bankReportDto.bankCodeFrom, bankReportDto.bankCodeTo);
            if (CollectionUtils.isEmpty(bankMasters)) {
                return new ResponseEntity(bankMasters, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else if (bankReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getBankDetailsPDF(bankReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (bankReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getBankDetailsExcel(bankReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(bankMasters, HttpStatus.OK);
    }

    @PostMapping("report/bank/bank-reconciliation")
    public ResponseEntity<InputStreamResource> getBankReconciliationReport(@RequestBody BankReportDto bankReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {

        List<Transaction> transactions = transactionReportService.getTransactionByBetweenDate(bankReportDto.getDateFrom(), bankReportDto.getDateTo());
        if (CollectionUtils.isEmpty(transactions)) {
            throw new EntityNotFoundException("No Transactions found for this selected date range");
        }
        return new ResponseEntity(transactions, HttpStatus.OK);
    }

}