package com.dfq.coeffi.cbs.report.accountCloserReports;


import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.report.PDFExcelFunction;
import com.dfq.coeffi.cbs.report.depositReports.DepositReportDto;
import com.dfq.coeffi.cbs.report.depositReports.DepositReportService;
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
public class DepositAccountCloserApi extends BaseController {


    private final DepositAccountCloserService depositAccountCloserService;
    private final PDFExcelFunction pdfExcelFunction;

    @Autowired
    private DepositAccountCloserApi(DepositAccountCloserService depositAccountCloserService, PDFExcelFunction pdfExcelFunction) {
        this.depositAccountCloserService = depositAccountCloserService;
        this.pdfExcelFunction = pdfExcelFunction;
    }

    @PostMapping("report/account-closer")
    public ResponseEntity<InputStreamResource> getFixedDepositReportByDate(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws DocumentException, ParseException, IOException, ServletException {
        if (depositReportDto.depositType.toString().equalsIgnoreCase("CHILDRENS_DEPOSIT")) {
            List<ChildrensDeposit> childrensDepositList = depositAccountCloserService.getAllChildrensDepositAccountCloser(depositReportDto.year);
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(childrensDepositList)) {
                    return new ResponseEntity(childrensDepositList, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(childrensDepositList, HttpStatus.OK);
            } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
                return pdfExcelFunction.getChildrensDepositAccountCloserPDF(depositReportDto, childrensDepositList);
            } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
                return pdfExcelFunction.getChildrensDepositAccountCloserExcel(depositReportDto, childrensDepositList, request, response);
            }
        }else if (depositReportDto.depositType.toString().equalsIgnoreCase("DOUBLE_SCHEME")) {
            List<DoubleScheme> doubleSchemes = depositAccountCloserService.getAllDoubleSchemeAccountCloser(depositReportDto.year);
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(doubleSchemes)) {
                    return new ResponseEntity(doubleSchemes, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(doubleSchemes, HttpStatus.OK);
            } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
                return pdfExcelFunction.getDoubleSchemeCloserPDF(depositReportDto, doubleSchemes);
            } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
                return pdfExcelFunction.getDoubleSchemeCloserExcel(depositReportDto, doubleSchemes, request, response);
            }
        }else if (depositReportDto.depositType.toString().equalsIgnoreCase("RECURRING_DEPOSIT")) {
            List<RecurringDeposit>  recurringDeposits = depositAccountCloserService.getAllRecurringDepositAccountCloser(depositReportDto.year);
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(recurringDeposits)) {
                    return new ResponseEntity(recurringDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(recurringDeposits, HttpStatus.OK);
            } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
                return pdfExcelFunction.getRecurringDepositCloserPDF(depositReportDto, recurringDeposits);
            } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
                return pdfExcelFunction.getRecurringDepositCloserExcel(depositReportDto, recurringDeposits, request, response);
            }
        }else if (depositReportDto.depositType.toString().equalsIgnoreCase("TERM_DEPOSIT")) {
            List<TermDeposit> termDeposits = depositAccountCloserService.getAllTermDepositAccountCloser(depositReportDto.year);
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(termDeposits)) {
                    return new ResponseEntity(termDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(termDeposits, HttpStatus.OK);
            } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
                return pdfExcelFunction.getTermDepositCloserPDF(depositReportDto, termDeposits);
            } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
                return pdfExcelFunction.getTermDepositCloserExcel(depositReportDto, termDeposits, request, response);
            }
        }else if (depositReportDto.depositType.toString().equalsIgnoreCase("FIXED_DEPOSIT")) {
            List<FixedDeposit> fixedDeposits= depositAccountCloserService.getAllFixedDepositAccountCloser(depositReportDto.year);
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(fixedDeposits)) {
                    return new ResponseEntity(fixedDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(fixedDeposits, HttpStatus.OK);
            } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
                return pdfExcelFunction.getFixedDepositCloserPDF(depositReportDto, fixedDeposits);
            } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
                return pdfExcelFunction.getFixedDepositCloserExcel(depositReportDto, fixedDeposits, request, response);
            }
        }else if (depositReportDto.depositType.toString().equalsIgnoreCase("SAVING_BANK_DEPOSIT")) {
            List<SavingsBankDeposit> savingsBankDeposits = depositAccountCloserService.getAllSavingsBankAccountCloser(depositReportDto.year);
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(savingsBankDeposits)) {
                    return new ResponseEntity(savingsBankDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(savingsBankDeposits, HttpStatus.OK);
            } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
                return pdfExcelFunction.getSavingBankCloserPDF(depositReportDto, savingsBankDeposits);
            } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
                return pdfExcelFunction.getSavingBankCloserExcel(depositReportDto, savingsBankDeposits, request, response);
            }
        }else if (depositReportDto.depositType.toString().equalsIgnoreCase("CURRENT_ACCOUNT")) {
            List<CurrentAccount> currentAccounts = depositAccountCloserService.getAllCurrentAccountCloser(depositReportDto.year);
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(currentAccounts)) {
                    return new ResponseEntity(currentAccounts, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(currentAccounts, HttpStatus.OK);
            } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
                return pdfExcelFunction.getCurrentAccountCloserPDF(depositReportDto, currentAccounts);
            } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
                return pdfExcelFunction.getCurrentAccountCloserExcel(depositReportDto, currentAccounts, request, response);
            }
        }
        return null;
    }

    @PostMapping("report/loan-closer")
    public ResponseEntity<InputStreamResource> getLoanReportByDate(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws DocumentException, ParseException, IOException, ServletException {
            List<Loan> loans = depositAccountCloserService.getAllLoanCloser(depositReportDto.year,depositReportDto.loanType);
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(loans)) {
                    return new ResponseEntity(loans, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(loans, HttpStatus.OK);
            }
        return null;
    }

}