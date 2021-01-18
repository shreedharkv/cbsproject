package com.dfq.coeffi.cbs.report;

import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.MemberType;
import com.dfq.coeffi.cbs.report.accountReports.AccountReportService;
import com.dfq.coeffi.cbs.report.bankReport.BankReportDto;
import com.dfq.coeffi.cbs.report.bankReport.BankReportService;
import com.dfq.coeffi.cbs.report.depositReports.DepositReportDto;
import com.dfq.coeffi.cbs.report.depositReports.DepositReportService;
import com.dfq.coeffi.cbs.report.memberReports.MemberReportDto;
import com.dfq.coeffi.cbs.report.memberReports.MemberReportService;
import com.dfq.coeffi.cbs.report.savingBankReports.SavingBankReportDto;
import com.dfq.coeffi.cbs.report.savingBankReports.SavingBankReportService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.dfq.coeffi.cbs.utils.GeneratePdfReport;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

@Service
public class PDFExcelFunction {

    private final DepositReportService depositReportService;
    private final MemberReportService memberReportService;
    private final SavingBankReportService savingBankReportService;
    private final AccountReportService accountReportService;
    private final BankReportService bankReportService;

    @Autowired
    private PDFExcelFunction(DepositReportService depositReportService, MemberReportService memberReportService,
                             SavingBankReportService savingBankReportService,AccountReportService accountReportService,
                             BankReportService bankReportService) {
        this.depositReportService = depositReportService;
        this.memberReportService = memberReportService;
        this.savingBankReportService = savingBankReportService;
        this.accountReportService = accountReportService;
        this.bankReportService = bankReportService;
    }

    public ResponseEntity getReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<FixedDeposit> fixedDepositList = depositReportService.getFixedDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.accountNumber,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            String fileName = "Fixed-Deposit-List_" + depositReportDto.inputDate;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Fixed-Deposit-List", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Date"));
            s.addCell(new Label(2, 0, "Application#"));
            s.addCell(new Label(3, 0, "Amount"));
            for (int i = 0; i < fixedDepositList.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + fixedDepositList.get(i).getId()));
                String getDateWithFormat = DateUtil.convertToDateString(fixedDepositList.get(i).getCreatedOn());
                s.addCell(new Label(j + 1, i + 1, "" + getDateWithFormat));
                s.addCell(new Label(j + 2, i + 1, "" + fixedDepositList.get(i).getApplicationNumber()));
                s.addCell(new Label(j + 3, i + 1, "" + fixedDepositList.get(i).getDepositAmount()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity getCurrentAccountReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<CurrentAccount> currentAccounts = depositReportService.getCurrentAccountReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo);
            String fileName = "Fixed-Deposit-List_" + depositReportDto.inputDate;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("current-account", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Date"));
            s.addCell(new Label(2, 0, "Application#"));
            s.addCell(new Label(3, 0, "Amount"));
            for (int i = 0; i < currentAccounts.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + currentAccounts.get(i).getId()));
                String getDateWithFormat = DateUtil.convertToDateString(currentAccounts.get(i).getCreatedOn());
                s.addCell(new Label(j + 1, i + 1, "" + getDateWithFormat));
                s.addCell(new Label(j + 2, i + 1, "" + currentAccounts.get(i).getApplicationNumber()));
                s.addCell(new Label(j + 3, i + 1, "" + currentAccounts.get(i).getDepositAmount()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity getChildrensDepositReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<ChildrensDeposit> childrensDeposits = depositReportService.getChildrensDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            String fileName = "Childrens-Deposit-List_" + depositReportDto.inputDate;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("childrens-deposit", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Date"));
            s.addCell(new Label(2, 0, "Application#"));
            s.addCell(new Label(3, 0, "Deposit Type"));
            s.addCell(new Label(4, 0, "Deposit Amount"));
            s.addCell(new Label(5, 0, "Interest Amount"));
            s.addCell(new Label(6, 0, "Maturity Amount"));
            s.addCell(new Label(7, 0, "Maturity Date"));
            for (int i = 0; i < childrensDeposits.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + childrensDeposits.get(i).getId()));
                String getDateWithFormat = DateUtil.convertToDateString(childrensDeposits.get(i).getCreatedOn());
                s.addCell(new Label(j + 1, i + 1, "" + getDateWithFormat));
                s.addCell(new Label(j + 2, i + 1, "" + childrensDeposits.get(i).getApplicationNumber()));
                s.addCell(new Label(j + 3, i + 1, "" + childrensDeposits.get(i).getDepositType()));
                s.addCell(new Label(j + 4, i + 1, "" + childrensDeposits.get(i).getDepositAmount()));
                s.addCell(new Label(j + 5, i + 1, "" + childrensDeposits.get(i).getInterestAmount()));
                s.addCell(new Label(j + 6, i + 1, "" + childrensDeposits.get(i).getMaturityAmount()));
                String maturityDate = DateUtil.convertToDateString(childrensDeposits.get(i).getMaturityDate());
                s.addCell(new Label(j + 7, i + 1, "" + maturityDate));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity getDoubleSchemeReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<DoubleScheme> doubleSchemeList = depositReportService.getDoubleSchemeReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            String fileName = "Double-Scheme-List_" + depositReportDto.inputDate;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("double-scheme", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Date"));
            s.addCell(new Label(2, 0, "Application#"));
            s.addCell(new Label(3, 0, "Deposit Type"));
            s.addCell(new Label(4, 0, "Deposit Amount"));
            s.addCell(new Label(5, 0, "Interest Amount"));
            s.addCell(new Label(6, 0, "Maturity Amount"));
            s.addCell(new Label(7, 0, "Maturity Date"));
            for (int i = 0; i < doubleSchemeList.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + doubleSchemeList.get(i).getId()));
                String getDateWithFormat = DateUtil.convertToDateString(doubleSchemeList.get(i).getCreatedOn());
                s.addCell(new Label(j + 1, i + 1, "" + getDateWithFormat));
                s.addCell(new Label(j + 2, i + 1, "" + doubleSchemeList.get(i).getApplicationNumber()));
                s.addCell(new Label(j + 3, i + 1, "" + doubleSchemeList.get(i).getDepositType()));
                s.addCell(new Label(j + 4, i + 1, "" + doubleSchemeList.get(i).getDepositAmount()));
                s.addCell(new Label(j + 5, i + 1, "" + doubleSchemeList.get(i).getInterestAmount()));
                s.addCell(new Label(j + 6, i + 1, "" + doubleSchemeList.get(i).getMaturityAmount()));
                String maturityDate = DateUtil.convertToDateString(doubleSchemeList.get(i).getMaturityDate());
                s.addCell(new Label(j + 7, i + 1, "" + maturityDate));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity getRecurringDepositReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<RecurringDeposit> recurringDepositList = depositReportService.getRecurringDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            String fileName = "Recurring-Deposit-List_" + depositReportDto.inputDate;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("recurring-deposit", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Date"));
            s.addCell(new Label(2, 0, "Application#"));
            s.addCell(new Label(3, 0, "Deposit Type"));
            s.addCell(new Label(4, 0, "Deposit Amount"));
            s.addCell(new Label(5, 0, "Interest Amount"));
            s.addCell(new Label(6, 0, "Maturity Amount"));
            s.addCell(new Label(7, 0, "Maturity Date"));
            for (int i = 0; i < recurringDepositList.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + recurringDepositList.get(i).getId()));
                String getDateWithFormat = DateUtil.convertToDateString(recurringDepositList.get(i).getCreatedOn());
                s.addCell(new Label(j + 1, i + 1, "" + getDateWithFormat));
                s.addCell(new Label(j + 2, i + 1, "" + recurringDepositList.get(i).getApplicationNumber()));
                s.addCell(new Label(j + 3, i + 1, "" + recurringDepositList.get(i).getDepositType()));
                s.addCell(new Label(j + 4, i + 1, "" + recurringDepositList.get(i).getDepositAmount()));
                s.addCell(new Label(j + 5, i + 1, "" + recurringDepositList.get(i).getInterestAmount()));
                s.addCell(new Label(j + 6, i + 1, "" + recurringDepositList.get(i).getMaturityAmount()));
                String maturityDate = DateUtil.convertToDateString(recurringDepositList.get(i).getMaturityDate());
                s.addCell(new Label(j + 7, i + 1, "" + maturityDate));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity getSavingsBankReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<SavingsBankDeposit> savingsBankDepositList = depositReportService.getSavingsBankDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType);
            String fileName = "Savings-Bank-List_" + depositReportDto.inputDate;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("saving-bank", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Date"));
            s.addCell(new Label(2, 0, "Application#"));
            s.addCell(new Label(3, 0, "Deposit Type"));
            s.addCell(new Label(4, 0, "Deposit Amount"));
            for (int i = 0; i < savingsBankDepositList.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + savingsBankDepositList.get(i).getId()));
                String getDateWithFormat = DateUtil.convertToDateString(savingsBankDepositList.get(i).getCreatedOn());
                s.addCell(new Label(j + 1, i + 1, "" + getDateWithFormat));
                s.addCell(new Label(j + 2, i + 1, "" + savingsBankDepositList.get(i).getApplicationNumber()));
                s.addCell(new Label(j + 3, i + 1, "" + savingsBankDepositList.get(i).getDepositType()));
                s.addCell(new Label(j + 4, i + 1, "" + savingsBankDepositList.get(i).getDepositAmount()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity getTermDepositReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<TermDeposit> termDepositList = depositReportService.getTermDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            String fileName = "Term-Deposit-List_" + depositReportDto.inputDate;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("term-deposit", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Date"));
            s.addCell(new Label(2, 0, "Application#"));
            s.addCell(new Label(3, 0, "Deposit Type"));
            s.addCell(new Label(4, 0, "Deposit Amount"));
            for (int i = 0; i < termDepositList.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + termDepositList.get(i).getId()));
                String getDateWithFormat = DateUtil.convertToDateString(termDepositList.get(i).getCreatedOn());
                s.addCell(new Label(j + 1, i + 1, "" + getDateWithFormat));
                s.addCell(new Label(j + 2, i + 1, "" + termDepositList.get(i).getApplicationNumber()));
                s.addCell(new Label(j + 3, i + 1, "" + termDepositList.get(i).getDepositType()));
                s.addCell(new Label(j + 4, i + 1, "" + termDepositList.get(i).getDepositAmount()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> newsReport(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<FixedDeposit> fixedDepositList = (List<FixedDeposit>) depositReportService.getFixedDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.accountNumber,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
        ByteArrayInputStream bis = GeneratePdfReport.fixedDepositPdfReport(fixedDepositList);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity<InputStreamResource> currentAccountPDFReport(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<CurrentAccount> currentAccounts = depositReportService.getCurrentAccountReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo);
        ByteArrayInputStream bis = GeneratePdfReport.currentAccountPdfReport(currentAccounts);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity<InputStreamResource> childrensDepositPDFReport(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<ChildrensDeposit> childrensDeposits = depositReportService.getChildrensDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
        ByteArrayInputStream bis = GeneratePdfReport.childrensDepositPdfReport(childrensDeposits);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity<InputStreamResource> doubleSchemePDFReport(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<DoubleScheme> doubleSchemeList = depositReportService.getDoubleSchemeReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
        ByteArrayInputStream bis = GeneratePdfReport.doubleSchemePdfReport(doubleSchemeList);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity<InputStreamResource> recurringDepositPDFReport(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<RecurringDeposit> recurringDepositList = depositReportService.getRecurringDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
        ByteArrayInputStream bis = GeneratePdfReport.recurringDepositPdfReport(recurringDepositList);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity<InputStreamResource> savingBankDepositPDFReport(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<SavingsBankDeposit> savingsBankDepositList = depositReportService.getSavingsBankDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType);
        ByteArrayInputStream bis = GeneratePdfReport.savingBankDepositPdfReport(savingsBankDepositList);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity<InputStreamResource> termDepositPDFReport(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<TermDeposit> termDepositList = depositReportService.getTermDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
        ByteArrayInputStream bis = GeneratePdfReport.termDepositPdfReport(termDepositList);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getLoanReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<Loan> loans = depositReportService.getLoanReportByDate(depositReportDto.inputDate);
            String fileName = "loans-List_" + depositReportDto.inputDate;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("term-deposit", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Date"));
            s.addCell(new Label(2, 0, "Application#"));
            s.addCell(new Label(3, 0, "SanctionedAmount"));
            s.addCell(new Label(4, 0, "Interest"));
            for (int i = 0; i < loans.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + loans.get(i).getId()));
                String getDateWithFormat = DateUtil.convertToDateString(loans.get(i).getCreatedOn());
                s.addCell(new Label(j + 1, i + 1, "" + getDateWithFormat));
                s.addCell(new Label(j + 2, i + 1, "" + loans.get(i).getApplicationNumber()));
                s.addCell(new Label(j + 3, i + 1, "" + loans.get(i).getLoanDetail().getSanctionedAmount()));
                s.addCell(new Label(j + 4, i + 1, "" + loans.get(i).getLoanDetail().getRateOfInterest()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> loanPDFReport(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<Loan> loans = depositReportService.getLoanReportByDate(depositReportDto.inputDate);
        ByteArrayInputStream bis = GeneratePdfReport.loanPdfReport(loans);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getMemberApplicationDetailsExcel(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<Member> members = memberReportService.getMemberApplicationDetailsByDate(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.numFrom, memberReportDto.numTo,
                    memberReportDto.applicationFrom, memberReportDto.applicationTo, memberReportDto.inputDate);
            String fileName = "Member-Application-Details_" + memberReportDto.dateFrom + "-" + memberReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Member-Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Date"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name & Address"));
            s.addCell(new Label(4, 0, "No. of Share Applied"));
            s.addCell(new Label(5, 0, "Share Amount"));
            for (int i = 0; i < members.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + members.get(i).getId()));
                String applicationDate = DateUtil.convertToDateString(members.get(i).getApplicationDate());
                s.addCell(new Label(j + 1, i + 1, "" + applicationDate));
                s.addCell(new Label(j + 2, i + 1, "" + members.get(i).getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + members.get(i).getName() + "" + members.get(0).getMemberPersonalDetail().getVillage()));
                s.addCell(new Label(j + 4, i + 1, "" + members.get(i).getSharesApplied()));
                s.addCell(new Label(j + 5, i + 1, "" + members.get(i).getSharesValue()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> memberApplicationDetailsPDF(@RequestBody MemberReportDto memberReportDto) throws IOException, DocumentException, ParseException {
        List<Member> members = memberReportService.getMemberApplicationDetailsByDate(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.numFrom, memberReportDto.numTo, memberReportDto.applicationFrom, memberReportDto.applicationTo, memberReportDto.inputDate);
        ByteArrayInputStream bis = GeneratePdfReport.memberApplicationDetailsPdfReport(members);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getNominalMemberDetailsExcel(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<Member> nominalMemberList = memberReportService.getNominalMemberList(memberReportDto.nominalMemberNumberFrom, memberReportDto.nominalMemberNumberTo);
            String fileName = "Nominal-Member-Details_" + memberReportDto.nominalMemberNumberFrom + "-" + memberReportDto.nominalMemberNumberTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Nominal-Member-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Nominal Member No."));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name & Address"));
            s.addCell(new Label(4, 0, "Application Date"));
            for (int i = 0; i < nominalMemberList.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + nominalMemberList.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + nominalMemberList.get(i).getMemberNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + nominalMemberList.get(i).getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + nominalMemberList.get(i).getName() + "" + nominalMemberList.get(0).getMemberPersonalDetail().getVillage()));
                String applicationDate = DateUtil.convertToDateString(nominalMemberList.get(i).getApplicationDate());
                s.addCell(new Label(j + 4, i + 1, "" + applicationDate));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> nominalMemberDetailsPDF(@RequestBody MemberReportDto memberReportDto) throws IOException, DocumentException, ParseException {
        List<Member> members = memberReportService.getNominalMemberList(memberReportDto.nominalMemberNumberFrom, memberReportDto.nominalMemberNumberTo);
        ByteArrayInputStream bis = GeneratePdfReport.nominalMemberDetailsPdfReport(members);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getCustomerDetailsExcel(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<Customer> customerList = memberReportService.getCustomerDetailsListByDate(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.customerIdFrom, memberReportDto.customerIdTo);
            String fileName = "Customer-Details_" + memberReportDto.dateFrom + "-" + memberReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Customer-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Customer ID"));
            s.addCell(new Label(2, 0, "Application Date"));
            s.addCell(new Label(3, 0, "Name & Address"));
            s.addCell(new Label(4, 0, "Age"));
            s.addCell(new Label(5, 0, "Telephone number"));
            for (int i = 0; i < customerList.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + customerList.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + customerList.get(i).getId()));
                String applicationDate = DateUtil.convertToDateString(customerList.get(i).getApplicationDate());
                s.addCell(new Label(j + 2, i + 1, "" + applicationDate));
                s.addCell(new Label(j + 3, i + 1, "" + customerList.get(i).getName() + "," + customerList.get(i).getCustomerPersonalDetails().getVillage()));
                s.addCell(new Label(j + 4, i + 1, "" + customerList.get(i).getAge()));
                s.addCell(new Label(j + 5, i + 1, "" + customerList.get(i).getCustomerPersonalDetails().getPhoneNumber()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getCustomerDetailsPdf(@RequestBody MemberReportDto memberReportDto) throws IOException, DocumentException, ParseException {
        List<Customer> customerList = memberReportService.getCustomerDetailsListByDate(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.customerIdFrom, memberReportDto.customerIdTo);
        ByteArrayInputStream bis = GeneratePdfReport.customerDetailsPdfReport(customerList);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getCurrentAccountApplicationDetailsExcel(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<CurrentAccount> currentAccountApplicationDetails = depositReportService.getCurrentAccountApplicationDetails(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.applicationFrom, memberReportDto.applicationTo, memberReportDto.accountNumberFrom, memberReportDto.accountNumberTo);
            String fileName = "aaa" + memberReportDto.dateFrom + "-" + memberReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Application number"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Application Date"));
            s.addCell(new Label(4, 0, "Name"));
            s.addCell(new Label(5, 0, "Address"));
            s.addCell(new Label(6, 0, "Occupation"));
            s.addCell(new Label(7, 0, "Age"));

            for (int i = 0; i < currentAccountApplicationDetails.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + currentAccountApplicationDetails.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + currentAccountApplicationDetails.get(i).getApplicationNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + currentAccountApplicationDetails.get(i).getMember().getCustomer().getId()));
                String applicationDate = DateUtil.convertToDateString(currentAccountApplicationDetails.get(i).getCreatedOn());
                s.addCell(new Label(j + 3, i + 1, "" + applicationDate));
                s.addCell(new Label(j + 4, i + 1, "" + currentAccountApplicationDetails.get(i).getMember().getName()));
                s.addCell(new Label(j + 5, i + 1, "" + currentAccountApplicationDetails.get(i).getMember().getMemberPersonalDetail().getVillage()));
                s.addCell(new Label(j + 6, i + 1, "" + currentAccountApplicationDetails.get(i).getMember().getOccupationCode()));
                s.addCell(new Label(j + 7, i + 1, "" + currentAccountApplicationDetails.get(i).getMember().getAge()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getCurrentAccountApplicationDetailsPdf(@RequestBody MemberReportDto memberReportDto) throws IOException, DocumentException, ParseException {
        List<CurrentAccount> currentAccountApplicationDetails = depositReportService.getCurrentAccountApplicationDetails(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.applicationFrom, memberReportDto.applicationTo, memberReportDto.accountNumberFrom, memberReportDto.accountNumberTo);
        ByteArrayInputStream bis = GeneratePdfReport.currentAccountApplicationDetailsPdfReport(currentAccountApplicationDetails);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getCurrentAccountMemberDetailsExcel(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<CurrentAccount> currentAccountApplicationDetails = depositReportService.getCurrentAccountApplicationDetails(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.applicationFrom, memberReportDto.applicationTo, memberReportDto.accountNumberFrom, memberReportDto.accountNumberTo);
            String fileName = "currentAccountApplicationDetails" + memberReportDto.dateFrom + "-" + memberReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "CA A/c number"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Tel. No."));

            for (int i = 0; i < currentAccountApplicationDetails.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + currentAccountApplicationDetails.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + currentAccountApplicationDetails.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + currentAccountApplicationDetails.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + currentAccountApplicationDetails.get(i).getMember().getName()));
                s.addCell(new Label(j + 4, i + 1, "" + currentAccountApplicationDetails.get(i).getMember().getMemberPersonalDetail().getVillage()));
                s.addCell(new Label(j + 5, i + 1, "" + currentAccountApplicationDetails.get(i).getMember().getMemberPersonalDetail().getPhoneNumber()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getCurrentAccountMemberDetailsPdf(@RequestBody MemberReportDto memberReportDto) throws IOException, DocumentException, ParseException {
        List<CurrentAccount> currentAccountApplicationDetails = depositReportService.getCurrentAccountApplicationDetails(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.applicationFrom, memberReportDto.applicationTo, memberReportDto.accountNumberFrom, memberReportDto.accountNumberTo);
        ByteArrayInputStream bis = GeneratePdfReport.currentAccountMemberDetailsPdfReport(currentAccountApplicationDetails);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity<InputStreamResource> getCurrentAccountLedgerDetailsPdf(@RequestBody MemberReportDto memberReportDto) throws IOException, DocumentException, ParseException {
        List<CurrentAccountTransaction> currentAccountLedgerDetails = depositReportService.getCurrentAccountLedgerDetails(memberReportDto.dateFrom, memberReportDto.dateTo,memberReportDto.accountNumber);
        ByteArrayInputStream bis = GeneratePdfReport.currentAccountLedgerDetailsPdfReport(currentAccountLedgerDetails);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getCurrentAccountLedgerDetailsExcel(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<CurrentAccountTransaction> currentAccountLedgerDetails = depositReportService.getCurrentAccountLedgerDetails(memberReportDto.dateFrom, memberReportDto.dateTo,memberReportDto.accountNumber);
            String fileName = "currentAccountLedgerDetails" + memberReportDto.dateFrom + "-" + memberReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "CA A/c number"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Tel. No."));

            for (int i = 0; i < currentAccountLedgerDetails.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + currentAccountLedgerDetails.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + currentAccountLedgerDetails.get(i).getCurrentAccount().getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + currentAccountLedgerDetails.get(i).getCurrentAccount().getMember().getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + currentAccountLedgerDetails.get(i).getCurrentAccount().getMember().getName()));
                s.addCell(new Label(j + 4, i + 1, "" + currentAccountLedgerDetails.get(i).getCurrentAccount().getMember().getMemberPersonalDetail().getVillage()));
                s.addCell(new Label(j + 5, i + 1, "" + currentAccountLedgerDetails.get(i).getCurrentAccount().getMember().getMemberPersonalDetail().getPhoneNumber()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getFixedDepositRegisterPdf(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<FixedDeposit> fixedDepositList = depositReportService.getFixedDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.accountNumber,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
        ByteArrayInputStream bis = GeneratePdfReport.fixedDepositRegisterPdfReport(fixedDepositList);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getFixedDepositRegisterExcel(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<FixedDeposit> fixedDepositList = depositReportService.getFixedDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.accountNumber,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            String fileName = "fixedDepositList" + depositReportDto.dateFrom + "-" + depositReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "CA A/c number"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Tel. No."));

            for (int i = 0; i < fixedDepositList.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + fixedDepositList.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + fixedDepositList.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + fixedDepositList.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + fixedDepositList.get(i).getMember().getName()));
                s.addCell(new Label(j + 4, i + 1, "" + fixedDepositList.get(i).getMember().getMemberPersonalDetail().getVillage()));
                s.addCell(new Label(j + 5, i + 1, "" + fixedDepositList.get(i).getMember().getMemberPersonalDetail().getPhoneNumber()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getChildrensDepositRegisterPdf(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<ChildrensDeposit> childrensDeposits = depositReportService.getChildrensDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
        ByteArrayInputStream bis = GeneratePdfReport.childrensDepositRegisterPdfReport(childrensDeposits);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getChildrensDepositRegisterExcel(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<ChildrensDeposit> childrensDeposits = depositReportService.getChildrensDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            String fileName = "childrensDeposits" + depositReportDto.dateFrom + "-" + depositReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "CA A/c number"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Tel. No."));

            for (int i = 0; i < childrensDeposits.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + childrensDeposits.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + childrensDeposits.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + childrensDeposits.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + childrensDeposits.get(i).getMember().getName()));
                s.addCell(new Label(j + 4, i + 1, "" + childrensDeposits.get(i).getMember().getMemberPersonalDetail().getVillage()));
                s.addCell(new Label(j + 5, i + 1, "" + childrensDeposits.get(i).getMember().getMemberPersonalDetail().getPhoneNumber()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getDoubleSchemeRegisterPdf(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<DoubleScheme> doubleSchemeList = depositReportService.getDoubleSchemeReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
        ByteArrayInputStream bis = GeneratePdfReport.doubleSchemePdfReport(doubleSchemeList);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getDoubleSchemeRegisterExcel(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<DoubleScheme> doubleSchemeList = depositReportService.getDoubleSchemeReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            String fileName = "doubleSchemeList" + depositReportDto.dateFrom + "-" + depositReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "CA A/c number"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Tel. No."));

            for (int i = 0; i < doubleSchemeList.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + doubleSchemeList.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + doubleSchemeList.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + doubleSchemeList.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + doubleSchemeList.get(i).getMember().getName()));
                s.addCell(new Label(j + 4, i + 1, "" + doubleSchemeList.get(i).getMember().getMemberPersonalDetail().getVillage()));
                s.addCell(new Label(j + 5, i + 1, "" + doubleSchemeList.get(i).getMember().getMemberPersonalDetail().getPhoneNumber()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getRecurringDepositRegisterPdf(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<RecurringDeposit> recurringDepositList = depositReportService.getRecurringDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
        ByteArrayInputStream bis = GeneratePdfReport.recurringDepositPdfReport(recurringDepositList);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getRecurringDepositRegisterExcel(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<RecurringDeposit> recurringDepositList = depositReportService.getRecurringDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            String fileName = "recurringDepositList" + depositReportDto.dateFrom + "-" + depositReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "CA A/c number"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Tel. No."));

            for (int i = 0; i < recurringDepositList.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + recurringDepositList.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + recurringDepositList.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + recurringDepositList.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + recurringDepositList.get(i).getMember().getName()));
                s.addCell(new Label(j + 4, i + 1, "" + recurringDepositList.get(i).getMember().getMemberPersonalDetail().getVillage()));
                s.addCell(new Label(j + 5, i + 1, "" + recurringDepositList.get(i).getMember().getMemberPersonalDetail().getPhoneNumber()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getTermDepositRegisterPdf(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<TermDeposit> termDeposits = depositReportService.getTermDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
        ByteArrayInputStream bis = GeneratePdfReport.termDepositPdfReport(termDeposits);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getTermDepositRegisterExcel(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<TermDeposit> termDeposits = depositReportService.getTermDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType,depositReportDto.accountNumberFrom,depositReportDto.accountNumberTo,depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            String fileName = "termDeposits" + depositReportDto.dateFrom + "-" + depositReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "CA A/c number"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Tel. No."));

            for (int i = 0; i < termDeposits.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + termDeposits.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + termDeposits.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + termDeposits.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + termDeposits.get(i).getMember().getName()));
                s.addCell(new Label(j + 4, i + 1, "" + termDeposits.get(i).getMember().getMemberPersonalDetail().getVillage()));
                s.addCell(new Label(j + 5, i + 1, "" + termDeposits.get(i).getMember().getMemberPersonalDetail().getPhoneNumber()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getSavingBankApplicationDetailsPDF(@RequestBody SavingBankReportDto savingBankReportDto) throws IOException, DocumentException, ParseException {
        List<SavingsBankDeposit> savingsBankDeposits= savingBankReportService.getSavingBankApplicationDetails(savingBankReportDto.dateFrom,savingBankReportDto.dateTo,savingBankReportDto.applicationNumberFrom,savingBankReportDto.applicationNumberTo);
        ByteArrayInputStream bis = GeneratePdfReport.savingBankDepositPdfReport(savingsBankDeposits);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getSavingBankApplicationDetailsExcel(@RequestBody SavingBankReportDto savingBankReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<SavingsBankDeposit> savingsBankDeposits= savingBankReportService.getSavingBankApplicationDetails(savingBankReportDto.dateFrom,savingBankReportDto.dateTo,savingBankReportDto.applicationNumberFrom,savingBankReportDto.applicationNumberTo);
            String fileName = "savingsBankDeposits" + savingBankReportDto.dateFrom + "-" + savingBankReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "CA A/c number"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Tel. No."));

            for (int i = 0; i < savingsBankDeposits.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + savingsBankDeposits.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + savingsBankDeposits.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + savingsBankDeposits.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + savingsBankDeposits.get(i).getMember().getName()));
                s.addCell(new Label(j + 4, i + 1, "" + savingsBankDeposits.get(i).getMember().getMemberPersonalDetail().getVillage()));
                s.addCell(new Label(j + 5, i + 1, "" + savingsBankDeposits.get(i).getMember().getMemberPersonalDetail().getPhoneNumber()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getSavingBankMemberDetailsPDF(@RequestBody SavingBankReportDto savingBankReportDto) throws IOException, DocumentException, ParseException {
        List<SavingsBankDeposit> savingsBankDeposits= savingBankReportService.getSavingBankMemberDetails(savingBankReportDto.accountNumberFrom,savingBankReportDto.accountNumberTo);
        ByteArrayInputStream bis = GeneratePdfReport.savingBankDepositPdfReport(savingsBankDeposits);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getSavingBankMemberDetailsExcel(@RequestBody SavingBankReportDto savingBankReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<SavingsBankDeposit> savingsBankDeposits= savingBankReportService.getSavingBankMemberDetails(savingBankReportDto.accountNumberFrom,savingBankReportDto.accountNumberTo);
            String fileName = "savingsBankDeposits" + savingBankReportDto.dateFrom + "-" + savingBankReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "SB A/c number"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Tel. No."));

            for (int i = 0; i < savingsBankDeposits.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + savingsBankDeposits.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + savingsBankDeposits.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + savingsBankDeposits.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + savingsBankDeposits.get(i).getMember().getName()));
                s.addCell(new Label(j + 4, i + 1, "" + savingsBankDeposits.get(i).getMember().getMemberPersonalDetail().getVillage()));
                s.addCell(new Label(j + 5, i + 1, "" + savingsBankDeposits.get(i).getMember().getMemberPersonalDetail().getPhoneNumber()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getSavingBankLedgerDetailsPDF(@RequestBody SavingBankReportDto savingBankReportDto) throws IOException, DocumentException, ParseException {
        List<SavingBankTransaction> sbLedgerDetails= savingBankReportService.getSBLedgerDetails(savingBankReportDto.accountNumber,savingBankReportDto.dateFrom,savingBankReportDto.dateTo);
        ByteArrayInputStream bis = GeneratePdfReport.savingBankLedgerDetailsPdfReport(sbLedgerDetails);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getSavingBankLedgerDetailsExcel(@RequestBody SavingBankReportDto savingBankReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<SavingBankTransaction> savingBankTransactions= savingBankReportService.getSBLedgerDetails(savingBankReportDto.accountNumber,savingBankReportDto.dateFrom,savingBankReportDto.dateTo);
            String fileName = "savingBankTransactions" + savingBankReportDto.dateFrom + "-" + savingBankReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("savingBankTransactions", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "SB A/c number"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name"));
            s.addCell(new Label(4, 0, "Balance"));
            s.addCell(new Label(5, 0, "TransactionDate"));

            for (int i = 0; i < savingBankTransactions.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + savingBankTransactions.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + savingBankTransactions.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + savingBankTransactions.get(i).getSavingsBankDeposit().getMember().getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + savingBankTransactions.get(i).getSavingsBankDeposit().getMember().getName()));
                s.addCell(new Label(j + 4, i + 1, "" + savingBankTransactions.get(i).getBalance()));
                s.addCell(new Label(j + 5, i + 1, "" + savingBankTransactions.get(i).getTransactionOn()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getSavingBankPassBookPDF(@RequestBody SavingBankReportDto savingBankReportDto) throws IOException, DocumentException, ParseException {
        List<SavingBankTransaction> sbPassBook= savingBankReportService.getSBLedgerDetails(savingBankReportDto.accountNumber,savingBankReportDto.dateFrom,savingBankReportDto.dateTo);
        ByteArrayInputStream bis = GeneratePdfReport.savingBankPassBookPdfReport(sbPassBook);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getSavingBankPassBookExcel(@RequestBody SavingBankReportDto savingBankReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<SavingBankTransaction> savingBankTransactions= savingBankReportService.getSBLedgerDetails(savingBankReportDto.accountNumber,savingBankReportDto.dateFrom,savingBankReportDto.dateTo);
            String fileName = "savingBankTransactions" + savingBankReportDto.dateFrom + "-" + savingBankReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("savingBankTransactions", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "SB A/c number"));
            s.addCell(new Label(2, 0, "Name"));
            s.addCell(new Label(3, 0, "TransactionDate"));
            s.addCell(new Label(4, 0, "Credit"));
            s.addCell(new Label(5, 0, "Debit"));
            s.addCell(new Label(6, 0, "Balance Amount"));
            for (int i = 0; i < savingBankTransactions.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + savingBankTransactions.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + savingBankTransactions.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + savingBankTransactions.get(i).getSavingsBankDeposit().getMember().getName()));
                s.addCell(new Label(j + 3, i + 1, "" + savingBankTransactions.get(i).getTransactionOn()));
                s.addCell(new Label(j + 4, i + 1, "" + savingBankTransactions.get(i).getCreditAmount()));
                s.addCell(new Label(j + 5, i + 1, "" + savingBankTransactions.get(i).getDebitAmount()));
                s.addCell(new Label(j + 6, i + 1, "" + savingBankTransactions.get(i).getBalance()));

                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getCashBookDetailsPDF(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<Transaction> transactions = accountReportService.getCashBookDetails(depositReportDto.dateFrom,depositReportDto.dateTo);
        ByteArrayInputStream bis = GeneratePdfReport.cashBookDetailsPdfReport(transactions );//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getCashBookDetailsExcel(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<Transaction> transactions = accountReportService.getCashBookDetails(depositReportDto.dateFrom,depositReportDto.dateTo);
            String fileName = "savingBankTransactions" + depositReportDto.dateFrom + "-" + depositReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("savingBankTransactions", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "SB A/c number"));
            s.addCell(new Label(2, 0, "TransferType"));
            s.addCell(new Label(3, 0, "TransactionDate"));
            s.addCell(new Label(4, 0, "Credit"));
            s.addCell(new Label(5, 0, "Debit"));
            s.addCell(new Label(6, 0, "Balance Amount"));
            for (int i = 0; i < transactions.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + transactions.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + transactions.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + transactions.get(i).getTransferType()));
                s.addCell(new Label(j + 3, i + 1, "" + transactions.get(i).getTransactionOn()));
                s.addCell(new Label(j + 4, i + 1, "" + transactions.get(i).getCreditAmount()));
                s.addCell(new Label(j + 5, i + 1, "" + transactions.get(i).getDebitAmount()));
                s.addCell(new Label(j + 6, i + 1, "" + transactions.get(i).getBalance()));

                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getDayBookDetailsPDF(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<Transaction> transactions = accountReportService.getDayBookDetails(depositReportDto.dateFrom,depositReportDto.dateTo);
        ByteArrayInputStream bis = GeneratePdfReport.dayBookDetailsPdfReport(transactions );//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getDayBookDetailsExcel(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<Transaction> transactions = accountReportService.getDayBookDetails(depositReportDto.dateFrom,depositReportDto.dateTo);
            String fileName = "savingBankTransactions" + depositReportDto.dateFrom + "-" + depositReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("savingBankTransactions", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "SB A/c number"));
            s.addCell(new Label(2, 0, "TransferType"));
            s.addCell(new Label(3, 0, "TransactionDate"));
            s.addCell(new Label(4, 0, "Credit"));
            s.addCell(new Label(5, 0, "Debit"));
            s.addCell(new Label(6, 0, "Balance Amount"));
            for (int i = 0; i < transactions.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + transactions.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + transactions.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + transactions.get(i).getTransferType()));
                s.addCell(new Label(j + 3, i + 1, "" + transactions.get(i).getTransactionOn()));
                s.addCell(new Label(j + 4, i + 1, "" + transactions.get(i).getCreditAmount()));
                s.addCell(new Label(j + 5, i + 1, "" + transactions.get(i).getDebitAmount()));
                s.addCell(new Label(j + 6, i + 1, "" + transactions.get(i).getBalance()));

                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getGeneralLedgerDetailsPDF(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<Transaction> transactions = accountReportService.getGeneralLedgerDetails(depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.accountHeads);
        ByteArrayInputStream bis = GeneratePdfReport.generalLedgerDetailsPdfReport(transactions );//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getGeneralLedgerDetailsExcel(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<Transaction> transactions = accountReportService.getGeneralLedgerDetails(depositReportDto.dateFrom,depositReportDto.dateTo,depositReportDto.accountHeads);
            String fileName = "savingBankTransactions" + depositReportDto.dateFrom + "-" + depositReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("savingBankTransactions", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "SB A/c number"));
            s.addCell(new Label(2, 0, "TransferType"));
            s.addCell(new Label(3, 0, "TransactionDate"));
            s.addCell(new Label(4, 0, "Credit"));
            s.addCell(new Label(5, 0, "Debit"));
            s.addCell(new Label(6, 0, "Balance Amount"));
            for (int i = 0; i < transactions.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + transactions.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + transactions.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + transactions.get(i).getTransferType()));
                s.addCell(new Label(j + 3, i + 1, "" + transactions.get(i).getTransactionOn()));
                s.addCell(new Label(j + 4, i + 1, "" + transactions.get(i).getCreditAmount()));
                s.addCell(new Label(j + 5, i + 1, "" + transactions.get(i).getDebitAmount()));
                s.addCell(new Label(j + 6, i + 1, "" + transactions.get(i).getBalance()));

                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getBankDetailsPDF(@RequestBody BankReportDto bankReportDto) throws IOException, DocumentException, ParseException {
        List<BankMaster> bankMasters = bankReportService.getBankDetails(bankReportDto.bankCodeFrom,bankReportDto.bankCodeTo);
        ByteArrayInputStream bis = GeneratePdfReport.getBankDetailsPdfReport(bankMasters );//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getBankDetailsExcel(@RequestBody BankReportDto bankReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<BankMaster> bankMasters = bankReportService.getBankDetails(bankReportDto.bankCodeFrom,bankReportDto.bankCodeTo);
            String fileName = "BankDetailsReport" + bankReportDto.bankCodeFrom + "-" + bankReportDto.bankCodeTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("savingBankTransactions", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Bank Code"));
            s.addCell(new Label(2, 0, "Bank Name"));
            s.addCell(new Label(3, 0, "Address "));
            s.addCell(new Label(4, 0, "Village"));
            s.addCell(new Label(5, 0, "Taluk"));
            s.addCell(new Label(6, 0, "Pincode"));
            s.addCell(new Label(7, 0, "Phone Number"));

            for (int i = 0; i < bankMasters.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + bankMasters.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + bankMasters.get(i).getBankCode()));
                s.addCell(new Label(j + 2, i + 1, "" + bankMasters.get(i).getBankName()));
                s.addCell(new Label(j + 3, i + 1, "" + bankMasters.get(i).getAddress()));
                s.addCell(new Label(j + 4, i + 1, "" + bankMasters.get(i).getVillage()));
                s.addCell(new Label(j + 5, i + 1, "" + bankMasters.get(i).getTaluk()));
                s.addCell(new Label(j + 6, i + 1, "" + bankMasters.get(i).getPinCode()));
                s.addCell(new Label(j + 7, i + 1, "" + bankMasters.get(i).getPhoneNumber()));

                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getMemberDetailsPDF(@RequestBody MemberReportDto memberReportDto) throws IOException, DocumentException, ParseException {
        List<Member> memberList = memberReportService.getMemberDetailsByMemberNumber(memberReportDto.memberNumberFrom, memberReportDto.memberNumberTo, MemberType.MEMBER);
        ByteArrayInputStream bis = GeneratePdfReport.getMemberDetailsPdfReport(memberList );//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getMemberDetailsExcel(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<Member> memberList = memberReportService.getMemberDetailsByMemberNumber(memberReportDto.memberNumberFrom, memberReportDto.memberNumberTo, MemberType.MEMBER);
            String fileName = "MemberDetailsReport" + memberReportDto.memberNumberFrom + "-" + memberReportDto.memberNumberFrom;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("savingBankTransactions", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Member Number"));
            s.addCell(new Label(2, 0, "Member Name"));
            s.addCell(new Label(3, 0, "Customer Id "));
            s.addCell(new Label(4, 0, "Share Applied"));
            s.addCell(new Label(5, 0, "Share Value"));

            for (int i = 0; i < memberList.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + memberList.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + memberList.get(i).getMemberNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + memberList.get(i).getName()));
                s.addCell(new Label(j + 3, i + 1, "" + memberList.get(i).getCustomer().getId()));
                s.addCell(new Label(j + 4, i + 1, "" + memberList.get(i).getSharesApplied()));
                s.addCell(new Label(j + 5, i + 1, "" + memberList.get(i).getSharesValue()));

                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity getChildrensDepositAccountCloserExcel(@RequestBody DepositReportDto depositReportDto,List<ChildrensDeposit> childrensDeposits, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            String fileName = "childrensDeposits" + depositReportDto.dateFrom + "-" + depositReportDto.dateTo;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "A/c number"));
            s.addCell(new Label(2, 0, "Existing A/c no"));
            s.addCell(new Label(3, 0, "Customer Id"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Closed Date"));
            for (int i = 0; i < childrensDeposits.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + childrensDeposits.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + childrensDeposits.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + childrensDeposits.get(i).getExgAccountNumber()));
                s.addCell(new Label(j + 3, i + 1, "" + childrensDeposits.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 4, i + 1, "" + childrensDeposits.get(i).getMember() +"\n"+childrensDeposits.get(i).getMember().getMemberPersonalDetail().getResidentialAddress()));
                s.addCell(new Label(j + 5, i + 1, "" + childrensDeposits.get(i).getAccountClosedOn()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getChildrensDepositAccountCloserPDF(@RequestBody DepositReportDto depositReportDto,List<ChildrensDeposit> childrensDeposits) throws IOException, DocumentException, ParseException {
        ByteArrayInputStream bis = GeneratePdfReport.getChildrensDepositAccountCloserPDFReport(childrensDeposits );//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    //25th March : Account Closer Report
    public ResponseEntity getDoubleSchemeCloserExcel(@RequestBody DepositReportDto depositReportDto,List<DoubleScheme> doubleSchemes,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            String fileName = "DoubleScheme" + depositReportDto.year + "-" + depositReportDto.year;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "A/c number"));
            s.addCell(new Label(2, 0, "Name"));
            s.addCell(new Label(3, 0, "Customer Id"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Closed Date"));
            for (int i = 0; i < doubleSchemes.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + doubleSchemes.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + doubleSchemes.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + doubleSchemes.get(i).getMember().getName()));
                s.addCell(new Label(j + 3, i + 1, "" + doubleSchemes.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 4, i + 1, "" + doubleSchemes.get(i).getMember() +"\n"+doubleSchemes.get(i).getMember().getMemberPersonalDetail().getResidentialAddress()));
                s.addCell(new Label(j + 5, i + 1, "" + doubleSchemes.get(i).getAccountClosedOn()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getDoubleSchemeCloserPDF(@RequestBody DepositReportDto depositReportDto,List<DoubleScheme> doubleSchemes) throws IOException, DocumentException, ParseException {
        ByteArrayInputStream bis = GeneratePdfReport.getDoubleSchemeCloserPDFReport(doubleSchemes);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=DoubleSchemeCloser.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getRecurringDepositCloserExcel(@RequestBody DepositReportDto depositReportDto,List<RecurringDeposit> recurringDeposits,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            String fileName = "DoubleScheme" + depositReportDto.year + "-" + depositReportDto.year;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "A/c number"));
            s.addCell(new Label(2, 0, "Name"));
            s.addCell(new Label(3, 0, "Customer Id"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Closed Date"));
            for (int i = 0; i < recurringDeposits.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + recurringDeposits.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + recurringDeposits.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + recurringDeposits.get(i).getMember().getName()));
                s.addCell(new Label(j + 3, i + 1, "" + recurringDeposits.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 4, i + 1, "" + recurringDeposits.get(i).getMember() +"\n"+recurringDeposits.get(i).getMember().getMemberPersonalDetail().getResidentialAddress()));
                s.addCell(new Label(j + 5, i + 1, "" + recurringDeposits.get(i).getAccountClosedOn()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getRecurringDepositCloserPDF(@RequestBody DepositReportDto depositReportDto,List<RecurringDeposit> recurringDeposits) throws IOException, DocumentException, ParseException {
        ByteArrayInputStream bis = GeneratePdfReport.getRecurringDepositCloserPDFReport(recurringDeposits);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=DoubleSchemeCloser.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getTermDepositCloserExcel(@RequestBody DepositReportDto depositReportDto,List<TermDeposit> termDeposits,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            String fileName = "termDeposits" + depositReportDto.year + "-" + depositReportDto.year;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "A/c number"));
            s.addCell(new Label(2, 0, "Name"));
            s.addCell(new Label(3, 0, "Customer Id"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Closed Date"));
            for (int i = 0; i < termDeposits.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + termDeposits.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + termDeposits.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + termDeposits.get(i).getMember().getName()));
                s.addCell(new Label(j + 3, i + 1, "" + termDeposits.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 4, i + 1, "" + termDeposits.get(i).getMember() +"\n"+termDeposits.get(i).getMember().getMemberPersonalDetail().getResidentialAddress()));
                s.addCell(new Label(j + 5, i + 1, "" + termDeposits.get(i).getAccountClosedOn()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getTermDepositCloserPDF(@RequestBody DepositReportDto depositReportDto,List<TermDeposit> termDeposits) throws IOException, DocumentException, ParseException {
        ByteArrayInputStream bis = GeneratePdfReport.getTermDepositCloserPDFReport(termDeposits);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=TermDeposit.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getFixedDepositCloserExcel(@RequestBody DepositReportDto depositReportDto,List<FixedDeposit> fixedDeposits,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            String fileName = "fixedDeposits" + depositReportDto.year + "-" + depositReportDto.year;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "A/c number"));
            s.addCell(new Label(2, 0, "Name"));
            s.addCell(new Label(3, 0, "Customer Id"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Closed Date"));
            for (int i = 0; i < fixedDeposits.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + fixedDeposits.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + fixedDeposits.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + fixedDeposits.get(i).getMember().getName()));
                s.addCell(new Label(j + 3, i + 1, "" + fixedDeposits.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 4, i + 1, "" + fixedDeposits.get(i).getMember() +"\n"+fixedDeposits.get(i).getMember().getMemberPersonalDetail().getResidentialAddress()));
                s.addCell(new Label(j + 5, i + 1, "" + fixedDeposits.get(i).getAccountClosedOn()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getFixedDepositCloserPDF(@RequestBody DepositReportDto depositReportDto,List<FixedDeposit> fixedDeposits) throws IOException, DocumentException, ParseException {
        ByteArrayInputStream bis = GeneratePdfReport.getFixedDepositCloserPDFReport(fixedDeposits);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=DoubleSchemeCloser.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getSavingBankCloserExcel(@RequestBody DepositReportDto depositReportDto,List<SavingsBankDeposit> savingsBankDeposits,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            String fileName = "DoubleScheme" + depositReportDto.year + "-" + depositReportDto.year;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "A/c number"));
            s.addCell(new Label(2, 0, "Name"));
            s.addCell(new Label(3, 0, "Customer Id"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Closed Date"));
            for (int i = 0; i < savingsBankDeposits.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + savingsBankDeposits.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + savingsBankDeposits.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + savingsBankDeposits.get(i).getMember().getName()));
                s.addCell(new Label(j + 3, i + 1, "" + savingsBankDeposits.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 4, i + 1, "" + savingsBankDeposits.get(i).getMember() +"\n"+savingsBankDeposits.get(i).getMember().getMemberPersonalDetail().getResidentialAddress()));
                s.addCell(new Label(j + 5, i + 1, "" + savingsBankDeposits.get(i).getAccountClosedOn()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getSavingBankCloserPDF(@RequestBody DepositReportDto depositReportDto,List<SavingsBankDeposit> savingsBankDeposits) throws IOException, DocumentException, ParseException {
        ByteArrayInputStream bis = GeneratePdfReport.getSavingBankCloserPDFReport(savingsBankDeposits);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=DoubleSchemeCloser.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    public ResponseEntity getCurrentAccountCloserExcel(@RequestBody DepositReportDto depositReportDto,List<CurrentAccount> currentAccounts,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            String fileName = "DoubleScheme" + depositReportDto.year + "-" + depositReportDto.year;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Application-Details", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "A/c number"));
            s.addCell(new Label(2, 0, "Name"));
            s.addCell(new Label(3, 0, "Customer Id"));
            s.addCell(new Label(4, 0, "Address"));
            s.addCell(new Label(5, 0, "Closed Date"));
            for (int i = 0; i < currentAccounts.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + currentAccounts.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + currentAccounts.get(i).getAccountNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + currentAccounts.get(i).getMember().getName()));
                s.addCell(new Label(j + 3, i + 1, "" + currentAccounts.get(i).getMember().getCustomer().getId()));
                s.addCell(new Label(j + 4, i + 1, "" + currentAccounts.get(i).getMember() +"\n"+currentAccounts.get(i).getMember().getMemberPersonalDetail().getResidentialAddress()));
                s.addCell(new Label(j + 5, i + 1, "" + currentAccounts.get(i).getAccountClosedOn()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> getCurrentAccountCloserPDF(@RequestBody DepositReportDto depositReportDto,List<CurrentAccount> currentAccounts) throws IOException, DocumentException, ParseException {
        ByteArrayInputStream bis = GeneratePdfReport.getCurrentAccountCloserPDFReport(currentAccounts);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=DoubleSchemeCloser.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

}

