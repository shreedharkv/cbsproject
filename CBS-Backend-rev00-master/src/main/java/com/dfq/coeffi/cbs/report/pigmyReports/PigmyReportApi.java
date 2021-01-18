package com.dfq.coeffi.cbs.report.pigmyReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction.PigmyDepositTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction.PigmyDepositTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.report.PDFExcelFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class PigmyReportApi extends BaseController {
    private final PigmyReportService pigmyReportService;
    private final PDFExcelFunction pdfExcelFunction;
    private final PigmyDepositTransactionService pigmyDepositTransactionService;

    @Autowired
    public PigmyReportApi(PigmyReportService pigmyReportService, PDFExcelFunction pdfExcelFunction, PigmyDepositTransactionService pigmyDepositTransactionService) {
        this.pigmyReportService = pigmyReportService;
        this.pdfExcelFunction = pdfExcelFunction;
        this.pigmyDepositTransactionService = pigmyDepositTransactionService;
    }

    @PostMapping("report/pigmy/application-details")
    public ResponseEntity<InputStreamResource> getPigmyapplicationDetailsByDate(@RequestBody PigmyReportDto pigmyReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (pigmyReportDto.reportType.equalsIgnoreCase("list")) {
            List<PigmyDeposit> pigmyDeposits = pigmyReportService.getPigmyDepositApplicatinByDate(pigmyReportDto.dateFrom,pigmyReportDto.dateTo,pigmyReportDto.applicationFrom,pigmyReportDto.applicationTo);
            if (CollectionUtils.isEmpty(pigmyDeposits)) {
                return new ResponseEntity(pigmyDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(pigmyDeposits, HttpStatus.OK);
        } /*else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.memberApplicationDetailsPDF(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getMemberApplicationDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        return null;
    }

    @PostMapping("report/pigmy/member-details")
    public ResponseEntity<InputStreamResource> getPigmyMemberDetailsByDate(@RequestBody PigmyReportDto pigmyReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (pigmyReportDto.reportType.equalsIgnoreCase("list")) {
            List<PigmyDeposit> pigmyDeposits = pigmyReportService.getPigmyDepositMemberByAccountNumber(pigmyReportDto.accountNumberFrom, pigmyReportDto.accountNumberTo);
            if (CollectionUtils.isEmpty(pigmyDeposits)) {
                return new ResponseEntity(pigmyDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(pigmyDeposits, HttpStatus.OK);
        } /*else if (pigmyReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.memberApplicationDetailsPDF(pigmyReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (pigmyReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getMemberApplicationDetailsExcel(pigmyReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        return null;
    }

    @PostMapping("report/pigmy/day-wise-deposit-details")
    public ResponseEntity<InputStreamResource> getPigmyTransactionDetailsByDate(@RequestBody PigmyReportDto pigmyReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (pigmyReportDto.reportType.equalsIgnoreCase("list")) {
            List<PigmyDepositTransaction> pigmyDeposits = pigmyReportService.getPigmyDepositByDate(pigmyReportDto.inputDate);
            if (CollectionUtils.isEmpty(pigmyDeposits)) {
                return new ResponseEntity(pigmyDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(pigmyDeposits, HttpStatus.OK);
        } /*else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.memberApplicationDetailsPDF(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getMemberApplicationDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        return null;
    }

    @PostMapping("report/pigmy/month-wise-deposit-details")
    public ResponseEntity<InputStreamResource> getPigmyTransactionDetailsByMonth(@RequestBody PigmyReportDto pigmyReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (pigmyReportDto.reportType.equalsIgnoreCase("list")) {
            List<PigmyDepositTransaction> pigmyDeposits = pigmyReportService.getPigmyDepositByMonth(pigmyReportDto.inputMonth, pigmyReportDto.accountNumber);
            if (CollectionUtils.isEmpty(pigmyDeposits)) {
                return new ResponseEntity(pigmyDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(pigmyDeposits, HttpStatus.OK);
        } /*else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.memberApplicationDetailsPDF(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getMemberApplicationDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        return null;
    }
}
