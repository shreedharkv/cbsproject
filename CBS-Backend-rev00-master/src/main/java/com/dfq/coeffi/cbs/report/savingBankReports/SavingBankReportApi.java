package com.dfq.coeffi.cbs.report.savingBankReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.report.PDFExcelFunction;
import com.dfq.coeffi.cbs.report.pigmyReports.PigmyReportDto;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
public class SavingBankReportApi extends BaseController {

    @Autowired
    private SavingBankReportService savingBankReportService;

    @Autowired
    private PDFExcelFunction pdfExcelFunction;

    @PostMapping("report/saving-bank/application-details")
    public ResponseEntity<InputStreamResource> getSavingBankApplicationDetailsByDate(@RequestBody SavingBankReportDto savingBankReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {
        if (savingBankReportDto.reportType.equalsIgnoreCase("list")) {
            List<SavingsBankDeposit> savingsBankDeposits= savingBankReportService.getSavingBankApplicationDetails(savingBankReportDto.dateFrom,savingBankReportDto.dateTo,savingBankReportDto.applicationNumberFrom,savingBankReportDto.applicationNumberTo);
            if (CollectionUtils.isEmpty(savingsBankDeposits)) {
                return new ResponseEntity(savingsBankDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(savingsBankDeposits, HttpStatus.OK);
        } else if (savingBankReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getSavingBankApplicationDetailsPDF(savingBankReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (savingBankReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getSavingBankApplicationDetailsExcel(savingBankReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/saving-bank/member-details")
    public ResponseEntity<InputStreamResource> getSavingBankMemberDetailsByDate(@RequestBody SavingBankReportDto savingBankReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {
        if (savingBankReportDto.reportType.equalsIgnoreCase("list")) {
            List<SavingsBankDeposit> savingsBankDeposits= savingBankReportService.getSavingBankMemberDetails(savingBankReportDto.accountNumberFrom,savingBankReportDto.accountNumberTo);
            if (CollectionUtils.isEmpty(savingsBankDeposits)) {
                return new ResponseEntity(savingsBankDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(savingsBankDeposits, HttpStatus.OK);
        } else if (savingBankReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getSavingBankMemberDetailsPDF(savingBankReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (savingBankReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getSavingBankMemberDetailsExcel(savingBankReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/saving-bank/ledger-details")
    public ResponseEntity<InputStreamResource> getSBLedgerDetailsByDate(@RequestBody SavingBankReportDto savingBankReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {
        if (savingBankReportDto.reportType.equalsIgnoreCase("list")) {
            List<SavingBankTransaction> savingBankTransactions= savingBankReportService.getSBLedgerDetails(savingBankReportDto.accountNumber,savingBankReportDto.dateFrom,savingBankReportDto.dateTo);
            if (CollectionUtils.isEmpty(savingBankTransactions)) {
                return new ResponseEntity(savingBankTransactions, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(savingBankTransactions, HttpStatus.OK);
        } else if (savingBankReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getSavingBankLedgerDetailsPDF(savingBankReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (savingBankReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getSavingBankLedgerDetailsExcel(savingBankReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/saving-bank/passbook-print")
    public ResponseEntity<InputStreamResource> getSBPassbookPrint(@RequestBody SavingBankReportDto savingBankReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {
        if (savingBankReportDto.reportType.equalsIgnoreCase("list")) {
            List<SavingBankTransaction> savingBankTransactions= savingBankReportService.getSBLedgerDetails(savingBankReportDto.accountNumber,savingBankReportDto.dateFrom,savingBankReportDto.dateTo);
            if (CollectionUtils.isEmpty(savingBankTransactions)) {
                return new ResponseEntity(savingBankTransactions, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(savingBankTransactions, HttpStatus.OK);
        } else if (savingBankReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getSavingBankPassBookPDF(savingBankReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (savingBankReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getSavingBankPassBookExcel(savingBankReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
