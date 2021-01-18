package com.dfq.coeffi.cbs.report.depositReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.report.PDFExcelFunction;
import com.dfq.coeffi.cbs.report.memberReports.MemberReportDto;
import com.dfq.coeffi.cbs.report.transactionReport.TransactionReportDto;
import com.dfq.coeffi.cbs.report.transactionReport.TransactionReportService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
public class DepositReportApi extends BaseController {
    private final DepositReportService depositReportService;
    private final PDFExcelFunction pdfExcelFunction;
    private final TransactionReportService transactionReportService;

    @Autowired
    private DepositReportApi(DepositReportService depositReportService, PDFExcelFunction pdfExcelFunction,
                             TransactionReportService transactionReportService) {
        this.depositReportService = depositReportService;
        this.pdfExcelFunction = pdfExcelFunction;
        this.transactionReportService = transactionReportService;
    }

    @PostMapping("report/fixed-deposit")
    public ResponseEntity<InputStreamResource> getFixedDepositReportByDate(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws DocumentException, ParseException, IOException {
        List<FixedDeposit> fixedDepositList;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            fixedDepositList = depositReportService.getFixedDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.accountNumber,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            if (CollectionUtils.isEmpty(fixedDepositList)) {
                return new ResponseEntity(fixedDepositList, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(fixedDepositList, HttpStatus.OK);
        } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            return pdfExcelFunction.newsReport(depositReportDto);
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getReport(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/current-account")
    public ResponseEntity<InputStreamResource> getMemberListByDate(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException {
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            List<CurrentAccount> currentAccounts = depositReportService.getCurrentAccountReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo);

            if (CollectionUtils.isEmpty(currentAccounts)) {
                return new ResponseEntity(currentAccounts, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(currentAccounts, HttpStatus.OK);
        } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.currentAccountPDFReport(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getCurrentAccountReport(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/childrens-deposit")
    public ResponseEntity<InputStreamResource> getChildrensDepositsListByDate(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            List<ChildrensDeposit> childrensDeposits = depositReportService.getChildrensDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);

            if (CollectionUtils.isEmpty(childrensDeposits)) {
                return new ResponseEntity(childrensDeposits, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(childrensDeposits, HttpStatus.OK);
        } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.childrensDepositPDFReport(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getChildrensDepositReport(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/double-scheme")
    public ResponseEntity<InputStreamResource> getDoubleSchemeListByDate(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<DoubleScheme> doubleSchemeList;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            doubleSchemeList = depositReportService.getDoubleSchemeReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            if (CollectionUtils.isEmpty(doubleSchemeList)) {
                return new ResponseEntity(doubleSchemeList, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(doubleSchemeList, HttpStatus.OK);
        } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.doubleSchemePDFReport(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getDoubleSchemeReport(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/recurring-deposit")
    public ResponseEntity<InputStreamResource> getRecurringDepositListByDate(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<RecurringDeposit> recurringDepositList;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            recurringDepositList = depositReportService.getRecurringDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            if (CollectionUtils.isEmpty(recurringDepositList)) {
                return new ResponseEntity(recurringDepositList, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(recurringDepositList, HttpStatus.OK);
        } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.recurringDepositPDFReport(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getRecurringDepositReport(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/saving-bank")
    public ResponseEntity<InputStreamResource> getSavingsBankDepositListByDate(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            List<SavingsBankDeposit> savingsBankDepositList = depositReportService.getSavingsBankDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType);
            if (CollectionUtils.isEmpty(savingsBankDepositList)) {
                return new ResponseEntity(savingsBankDepositList, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(savingsBankDepositList, HttpStatus.OK);
        } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.savingBankDepositPDFReport(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getSavingsBankReport(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/term-deposit")
    public ResponseEntity<InputStreamResource> getTermDepositListByDate(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            List<TermDeposit> termDepositList = depositReportService.getTermDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            if (CollectionUtils.isEmpty(termDepositList)) {
                return new ResponseEntity(termDepositList, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(termDepositList, HttpStatus.OK);
        } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.termDepositPDFReport(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getTermDepositReport(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/loan")
    public ResponseEntity<InputStreamResource> getLoanListByDate(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            List<Loan> loans = depositReportService.getLoanReportByDate(depositReportDto.inputDate);
            if (CollectionUtils.isEmpty(loans)) {
                return new ResponseEntity(loans, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(loans, HttpStatus.OK);
        } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.loanPDFReport(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getLoanReport(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * CA Application Details
     */

    @PostMapping("report/current-account/application-details")
    public ResponseEntity<InputStreamResource> getCurrentAccountDetails(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (memberReportDto.reportType.equalsIgnoreCase("list")) {
            List<CurrentAccount> currentAccountApplicationDetails = depositReportService.getCurrentAccountApplicationDetails(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.applicationFrom, memberReportDto.applicationTo, memberReportDto.accountNumberFrom, memberReportDto.accountNumberTo);
            if (CollectionUtils.isEmpty(currentAccountApplicationDetails)) {
                return new ResponseEntity(currentAccountApplicationDetails, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(currentAccountApplicationDetails, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getCurrentAccountApplicationDetailsPdf(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getCurrentAccountApplicationDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/current-account/member-details")
    public ResponseEntity<InputStreamResource> getCurrentAccountMemberDetails(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (memberReportDto.reportType.equalsIgnoreCase("list")) {
            List<CurrentAccount> currentAccountApplicationDetails = depositReportService.getCurrentAccountApplicationDetails(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.applicationFrom, memberReportDto.applicationTo, memberReportDto.accountNumberFrom, memberReportDto.accountNumberTo);
            if (CollectionUtils.isEmpty(currentAccountApplicationDetails)) {
                return new ResponseEntity(currentAccountApplicationDetails, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(currentAccountApplicationDetails, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getCurrentAccountMemberDetailsPdf(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getCurrentAccountMemberDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/current-account/ledger-details")
    public ResponseEntity<InputStreamResource> getCurrentAccountLedgerDetails(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {

        List<CurrentAccountTransaction> currentAccountLedgerDetails = null;
        if (memberReportDto.reportType.equalsIgnoreCase("list")) {

            currentAccountLedgerDetails = depositReportService.getCurrentAccountLedgerDetails(memberReportDto.getDateFrom(), memberReportDto.getDateTo(), memberReportDto.accountNumber);

            if (CollectionUtils.isEmpty(currentAccountLedgerDetails)) {
                return new ResponseEntity(currentAccountLedgerDetails, HttpStatus.NO_CONTENT);
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getCurrentAccountLedgerDetailsPdf(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getCurrentAccountLedgerDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(currentAccountLedgerDetails, HttpStatus.OK);
    }

    /**
     * Deposit Register Module
     * Maturity Register
     * Due Date Register
     */

    @PostMapping("report/deposit-register/fixed-deposit")
    public ResponseEntity<InputStreamResource> getFixedDepositMaturityRegisterDetails(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<FixedDeposit> fixedDepositList = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            fixedDepositList = depositReportService.getFixedDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.accountNumber,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            if (CollectionUtils.isEmpty(fixedDepositList)) {
                return new ResponseEntity(fixedDepositList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getFixedDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getFixedDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(fixedDepositList, HttpStatus.OK);
    }

    @PostMapping("report/deposit-register/current-account")
    public ResponseEntity<InputStreamResource> getCurrentAccountMaturityRegisterDetails(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<CurrentAccount> currentAccounts = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            currentAccounts = depositReportService.getCurrentAccountReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo);

            if (CollectionUtils.isEmpty(currentAccounts)) {
                return new ResponseEntity(currentAccounts, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(currentAccounts, HttpStatus.OK);
        }
        return new ResponseEntity(currentAccounts, HttpStatus.OK);
    }

    @PostMapping("report/deposit-register/childrens-deposit")
    public ResponseEntity<InputStreamResource> getChildrensDepositMaturityRegisterDetails(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<ChildrensDeposit> childrensDeposits = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            childrensDeposits = depositReportService.getChildrensDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            if (CollectionUtils.isEmpty(childrensDeposits)) {
                return new ResponseEntity(childrensDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(childrensDeposits, HttpStatus.OK);
        }else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getChildrensDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getChildrensDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(childrensDeposits, HttpStatus.OK);
    }

    @PostMapping("report/deposit-register/double-scheme")
    public ResponseEntity<InputStreamResource> getDoubleSchemeRegisterDetails(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<DoubleScheme> doubleSchemeList = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            doubleSchemeList = depositReportService.getDoubleSchemeReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            if (CollectionUtils.isEmpty(doubleSchemeList)) {
                return new ResponseEntity(doubleSchemeList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(doubleSchemeList, HttpStatus.OK);
        }
        else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getDoubleSchemeRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getDoubleSchemeRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(doubleSchemeList, HttpStatus.OK);
    }

    @PostMapping("report/deposit-register/recurring-deposit")
    public ResponseEntity<InputStreamResource> getRecurringDepositRegisterDetails(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<RecurringDeposit> recurringDepositList = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            recurringDepositList = depositReportService.getRecurringDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            if (CollectionUtils.isEmpty(recurringDepositList)) {
                return new ResponseEntity(recurringDepositList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getRecurringDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getRecurringDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(recurringDepositList, HttpStatus.OK);
    }

    @PostMapping("report/deposit-register/term-deposit")
    public ResponseEntity<InputStreamResource> getTermDepositRegisterDetails(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<TermDeposit> termDeposits = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            termDeposits = depositReportService.getTermDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            if (CollectionUtils.isEmpty(termDeposits)) {
                return new ResponseEntity(termDeposits, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(termDeposits, HttpStatus.OK);
    }


    @PostMapping("report/deposit-ledger/saving_bank_deposit")
    public ResponseEntity<InputStreamResource> getSavingBankDepositRegisterDetails(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<SavingsBankDeposit> savingsBankDepositList = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {

            savingsBankDepositList = depositReportService.getSavingBankDepositLedgerReport(DepositType.SAVING_BANK_DEPOSIT, depositReportDto.accountNumber);

            if (CollectionUtils.isEmpty(savingsBankDepositList)) {
                return new ResponseEntity(savingsBankDepositList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(savingsBankDepositList, HttpStatus.OK);
    }

    @PostMapping("report/deposit-ledger/children_deposit")
    public ResponseEntity<InputStreamResource> getChildrenDepositLedgerReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<ChildrensDeposit> childrensDepositList = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {

            childrensDepositList = depositReportService.getChildrenDepositLedgerReport(DepositType.CHILDRENS_DEPOSIT, depositReportDto.accountNumber);

            if (CollectionUtils.isEmpty(childrensDepositList)) {
                return new ResponseEntity(childrensDepositList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(childrensDepositList, HttpStatus.OK);
    }

    @PostMapping("report/deposit-ledger/fixed_deposit")
    public ResponseEntity<InputStreamResource> getFixedDepositLedgerReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<FixedDeposit> fixedDepositList = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {

            fixedDepositList = depositReportService.getFixedDepositLedgerReport(DepositType.FIXED_DEPOSIT, depositReportDto.accountNumber);

            if (CollectionUtils.isEmpty(fixedDepositList)) {
                return new ResponseEntity(fixedDepositList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(fixedDepositList, HttpStatus.OK);
    }

    @PostMapping("report/deposit-ledger/current_account")
    public ResponseEntity<InputStreamResource> getCurrentAccountDepositLedgerReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<CurrentAccount> currentAccountList = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {

            currentAccountList = depositReportService.getCurrentAccountDepositLedgerReport(DepositType.CURRENT_ACCOUNT, depositReportDto.accountNumber);

            if (CollectionUtils.isEmpty(currentAccountList)) {
                return new ResponseEntity(currentAccountList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(currentAccountList, HttpStatus.OK);
    }

    @PostMapping("report/deposit-ledger/double_scheme")
    public ResponseEntity<InputStreamResource> getDoubleSchemeDepositLedgerReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<DoubleScheme> doubleSchemeList = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {

            doubleSchemeList = depositReportService.getDoubleSchemeDepositLedgerReport(DepositType.DOUBLE_SCHEME, depositReportDto.accountNumber);

            if (CollectionUtils.isEmpty(doubleSchemeList)) {
                return new ResponseEntity(doubleSchemeList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(doubleSchemeList, HttpStatus.OK);
    }

    @PostMapping("report/deposit-ledger/pigmy_deposit")
    public ResponseEntity<InputStreamResource> getPigmyDepositLedgerReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<PigmyDeposit> pigmyDepositList = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {

            pigmyDepositList = depositReportService.getPigmyDepositLedgerReport(DepositType.PIGMY_DEPOSIT, depositReportDto.accountNumber);

            if (CollectionUtils.isEmpty(pigmyDepositList)) {
                return new ResponseEntity(pigmyDepositList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(pigmyDepositList, HttpStatus.OK);
    }

    @PostMapping("report/deposit-ledger/recurring_deposit")
    public ResponseEntity<InputStreamResource> getRecurringDepositLedgerReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<RecurringDeposit> recurringDepositList = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {

            recurringDepositList = depositReportService.getRecurringDepositLedgerReport(DepositType.RECURRING_DEPOSIT, depositReportDto.accountNumber);

            if (CollectionUtils.isEmpty(recurringDepositList)) {
                return new ResponseEntity(recurringDepositList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(recurringDepositList, HttpStatus.OK);
    }

    @PostMapping("report/deposit-ledger/term_deposit")
    public ResponseEntity<InputStreamResource> getTermDepositLedgerReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<TermDeposit> termDepositList = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {

            termDepositList = depositReportService.getTermDepositLedgerReport(DepositType.TERM_DEPOSIT, depositReportDto.accountNumber);

            if (CollectionUtils.isEmpty(termDepositList)) {
                return new ResponseEntity(termDepositList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterPdf(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getTermDepositRegisterExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(termDepositList, HttpStatus.OK);
    }

    @PostMapping("deposit-ledger")
    public ResponseEntity<Transaction> getTransactionByDateAndLedgerAndAccountNumber(@RequestBody TransactionReportDto transactionReportDto){

        List<Transaction> transactionList = transactionReportService.getTransactionByDateAndLedgerAndAccountNumber(transactionReportDto.getDateFrom(), transactionReportDto.getDateTo(), transactionReportDto.getLedger(), transactionReportDto.getAccountNumber());
        if (CollectionUtils.isEmpty(transactionList)) {
            throw new EntityNotFoundException("No Transaction found for this " +transactionReportDto.getAccountNumber());
        }
        return new ResponseEntity(transactionList, HttpStatus.OK);
    }
}

