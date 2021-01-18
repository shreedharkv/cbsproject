package com.dfq.coeffi.cbs.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;


public class GeneratePdfReport extends PdfPageEventHelper {

    public GeneratePdfReport() throws ParseException {
    }

    /**
     * Function to generate income pdf report
     *
     * @param fixedDeposits
     * @return pdf file
     * @throws IOException
     */
    public static ByteArrayInputStream fixedDepositPdfReport(List<FixedDeposit> fixedDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Date", "Appln #", "Amount");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            for (FixedDeposit fixedDeposit : fixedDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(fixedDeposit.getCreatedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getApplicationNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getDepositAmount()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

     /*       Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font font1 = new Font(Font.FontFamily.COURIER, 10, Font.BOLD);
            Image img1 = Image.getInstance("https://res.cloudinary.com/hanumanth-cloudinary/image/upload/v1543079706/orileo_logo.png");
            img1.scaleAbsolute(50f, 50f);
            img1.scaleToFit(80f, 80f);
            img1.setAbsolutePosition(110, 755);
            document.add(img1);
            para = new Paragraph("Orileo Technologies", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            para = new Paragraph("Jayanagara,Bangalore -560004", font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);*/

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            para = new Paragraph("Fixed Deposit List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream currentAccountPdfReport(List<CurrentAccount> currentAccounts) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Date", "Appln #", "Amount");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            for (CurrentAccount currentAccount : currentAccounts) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(currentAccount.getCreatedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getApplicationNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getBalance()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();
            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);
            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Current Account List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream childrensDepositPdfReport(List<ChildrensDeposit> childrensDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Date", "Appln #", "Amount");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (ChildrensDeposit childrensDeposit : childrensDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(childrensDeposit.getCreatedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(childrensDeposit.getApplicationNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(childrensDeposit.getDepositAmount()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Childrens Deposit List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream doubleSchemePdfReport(List<DoubleScheme> doubleSchemes) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Date", "Appln #", "Amount");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (DoubleScheme doubleScheme : doubleSchemes) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(doubleScheme.getCreatedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(doubleScheme.getApplicationNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(doubleScheme.getDepositAmount()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Double Scheme Deposit List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream recurringDepositPdfReport(List<RecurringDeposit> recurringDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Date", "Appln #", "Amount");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (RecurringDeposit recurringDeposit : recurringDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(recurringDeposit.getCreatedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(recurringDeposit.getApplicationNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(recurringDeposit.getDepositAmount()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Recurring Deposit List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream savingBankDepositPdfReport(List<SavingsBankDeposit> savingsBankDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Date", "Appln #", "Amount");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (SavingsBankDeposit savingsBankDeposit : savingsBankDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(savingsBankDeposit.getCreatedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getApplicationNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getBalance()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();
            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);
            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Saving Bank List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream termDepositPdfReport(List<TermDeposit> termDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Date", "Appln #", "Amount");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (TermDeposit termDeposit : termDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(termDeposit.getCreatedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(termDeposit.getApplicationNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(termDeposit.getDepositAmount()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Term Deposit List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream loanPdfReport(List<Loan> loans) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Date", "Appln #", "Sanctioned Amount");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            BigDecimal totalAmount = new BigDecimal(0);
            for (Loan loan : loans) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(loan.getCreatedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(loan.getApplicationNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(loan.getLoanDetail().getSanctionedAmount()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                totalAmount = loan.getLoanDetail().getSanctionedAmount().add(totalAmount);
            }

            PdfPCell cell;
            cell = new PdfPCell(new Phrase("Total", headFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(totalAmount), headFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            System.out.println(totalAmount);
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Loan List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream memberReport(List<Member> members) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("Sr No", "Member No", "Customer ID", "Name", "Family Member Name", "Address", "Share Amount");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            BigDecimal totalAmount = new BigDecimal(0);
            for (Member member : members) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getMemberNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getCustomer().getId()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getMemberFamilyDetails().get(0).getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getSharesValue()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Members Shares Schedule Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream memberApplicationDetailsPdfReport(List<Member> members) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Member#", "Date", "Customer ID", "Name & Address", "No. of Shares Applied", "Share Amount");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            BigDecimal totalAmount = new BigDecimal(0);
            for (Member member : members) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getMemberNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(member.getApplicationDate());

                cell = new PdfPCell(new Phrase(String.valueOf(getDateWithFormat), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getCustomer().getId()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getName() + "" + member.getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getSharesApplied()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getSharesValue()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Members Application Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream nominalMemberDetailsPdfReport(List<Member> members) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Member#", "Customer ID", "Name & Address", "Date");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            BigDecimal totalAmount = new BigDecimal(0);
            for (Member member : members) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getMemberNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getCustomer().getId()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getName() + "" + member.getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(member.getApplicationDate());

                cell = new PdfPCell(new Phrase(String.valueOf(getDateWithFormat), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Nominal Member Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream customerDetailsPdfReport(List<Customer> customers) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Customer ID", "Application Date", "Name & Address", "Age", "Tel. No.");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (Customer customer : customers) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(customer.getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(customer.getApplicationDate());
                cell = new PdfPCell(new Phrase(String.valueOf(getDateWithFormat), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(customer.getName() + "," + customer.getCustomerPersonalDetails().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(customer.getAge()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(customer.getCustomerPersonalDetails().getPhoneNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Customer Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream currentAccountApplicationDetailsPdfReport(List<CurrentAccount> currentAccounts) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Application number", "Customer ID", "Application Date", "Name", "Address", "Occupation", "Age");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2, 2, 1});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (CurrentAccount currentAccount : currentAccounts) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getApplicationNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(currentAccount.getCreatedOn());
                cell = new PdfPCell(new Phrase(String.valueOf(getDateWithFormat), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getOccupationCode()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getAge()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Current Account Application Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream currentAccountMemberDetailsPdfReport(List<CurrentAccount> currentAccounts) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number", "Customer ID", "Name", "Address", "Telephone");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (CurrentAccount currentAccount : currentAccounts) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getMemberPersonalDetail().getPhoneNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Current Account Application Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream currentAccountLedgerDetailsPdfReport(List<CurrentAccountTransaction> currentAccountTransactions) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number", "Customer ID", "Name", "Address", "Telephone");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (CurrentAccountTransaction currentAccountTransaction : currentAccountTransactions) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccountTransaction.getCurrentAccount().getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccountTransaction.getCurrentAccount().getMember().getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccountTransaction.getCurrentAccount().getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccountTransaction.getCurrentAccount().getMember().getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccountTransaction.getCurrentAccount().getMember().getMemberPersonalDetail().getPhoneNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Current Account Ledger Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream fixedDepositRegisterPdfReport(List<FixedDeposit> fixedDepositList) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number", "Customer ID", "Name", "Address", "Telephone");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (FixedDeposit fixedDeposit: fixedDepositList) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getMember().getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getMember().getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getMember().getMemberPersonalDetail().getPhoneNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Fixed Deposit Register", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream childrensDepositRegisterPdfReport(List<ChildrensDeposit> childrensDepositList) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number", "Customer ID", "Name", "Address", "Telephone");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (ChildrensDeposit childrensDeposit: childrensDepositList) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(childrensDeposit.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(childrensDeposit.getMember().getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(childrensDeposit.getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(childrensDeposit.getMember().getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(childrensDeposit.getMember().getMemberPersonalDetail().getPhoneNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Childrens Deposit Register", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream doubleSchemeRegisterPdfReport(List<DoubleScheme> doubleSchemes) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number", "Customer ID", "Name", "Address", "Telephone");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (DoubleScheme doubleScheme: doubleSchemes) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(doubleScheme.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(doubleScheme.getMember().getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(doubleScheme.getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(doubleScheme.getMember().getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(doubleScheme.getMember().getMemberPersonalDetail().getPhoneNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Double Scheme Register", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream recurringDepositRegisterPdfReport(List<RecurringDeposit> recurringDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number", "Customer ID", "Name", "Address", "Telephone");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (RecurringDeposit recurringDeposit: recurringDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(recurringDeposit.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(recurringDeposit.getMember().getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(recurringDeposit.getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(recurringDeposit.getMember().getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(recurringDeposit.getMember().getMemberPersonalDetail().getPhoneNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Recurring Deposit Register", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream termDepositRegisterPdfReport(List<TermDeposit> termDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number", "Customer ID", "Name", "Address", "Telephone");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (TermDeposit termDeposit: termDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(termDeposit.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(termDeposit.getMember().getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(termDeposit.getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(termDeposit.getMember().getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(termDeposit.getMember().getMemberPersonalDetail().getPhoneNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Term Deposit Register", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream savingsBankDepositsPdfReport(List<SavingsBankDeposit> savingsBankDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number", "Customer ID", "Name", "Address", "Telephone");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (SavingsBankDeposit savingsBankDeposit: savingsBankDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getMember().getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getMember().getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getMember().getMemberPersonalDetail().getPhoneNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Saving Bank Application Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream savingBankMemberDetailsPdfReport(List<SavingsBankDeposit> savingsBankDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number", "Customer ID", "Name", "Address", "Telephone");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (SavingsBankDeposit savingsBankDeposit: savingsBankDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getMember().getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getMember().getMemberPersonalDetail().getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getMember().getMemberPersonalDetail().getPhoneNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Saving Bank Member Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream savingBankLedgerDetailsPdfReport(List<SavingBankTransaction> sbLedgerDetails) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number", "Customer ID", "Name", "Balance", "TransactionDate");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (SavingBankTransaction savingBankTransaction: sbLedgerDetails) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingBankTransaction.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingBankTransaction.getSavingsBankDeposit().getMember().getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingBankTransaction.getSavingsBankDeposit().getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingBankTransaction.getBalance()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingBankTransaction.getTransactionOn()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Saving Bank Member Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream savingBankPassBookPdfReport(List<SavingBankTransaction> sbPassBookDetails) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number", "Name","TransactionDate","Credit","Debit","Balance");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2,2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (SavingBankTransaction sbPassBook: sbPassBookDetails) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(sbPassBook.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(sbPassBook.getSavingsBankDeposit().getMember().getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(sbPassBook.getTransactionOn()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(sbPassBook.getCreditAmount()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(sbPassBook.getDebitAmount()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(sbPassBook.getBalance()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Saving Bank PassBook Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream cashBookDetailsPdfReport(List<Transaction> transactions) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number","TransactionDate","Credit","Debit","Balance","TransferType");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2,2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (Transaction transaction: transactions) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getTransactionOn()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getCreditAmount()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getDebitAmount()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getBalance()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getTransferType()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Cash Book Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream dayBookDetailsPdfReport(List<Transaction> transactions) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number","TransactionDate","Credit","Debit","Balance","TransferType");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2,2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (Transaction transaction: transactions) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getTransactionOn()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getCreditAmount()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getDebitAmount()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getBalance()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getTransferType()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Day Book Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream generalLedgerDetailsPdfReport(List<Transaction> transactions) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Account number","TransactionDate","Credit","Debit","Balance","TransferType");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2,2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (Transaction transaction: transactions) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getTransactionOn()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getCreditAmount()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getDebitAmount()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getBalance()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(transaction.getTransferType()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("General Ledger Details", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream getBankDetailsPdfReport(List<BankMaster> bankMasters ) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Bank Code","Bank Name","Address","Village","Taluk","Pincode","Phone number");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2,2,2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (BankMaster bankMaster: bankMasters) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(bankMaster.getBankCode()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(bankMaster.getBankName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(bankMaster.getAddress()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(bankMaster.getVillage()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(bankMaster.getTaluk()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(bankMaster.getPinCode()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(bankMaster.getPhoneNumber()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Bank Details Reports", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream getMemberDetailsPdfReport(List<Member> members) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            List<String> headers = Arrays.asList("No", "Member number","Member Name","Customer Id","Share Applied","Share Value");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 2, 2, 2, 2, 2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(hcell);
            }
            int i = 1;
            for (Member member: members) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getMemberNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getName()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getCustomer().getId()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getSharesApplied()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(member.getSharesValue()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);


            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Member Details Reports", f);
            c.setBackground(BaseColor.LIGHT_GRAY);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph paragraph;
            Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            paragraph = new Paragraph("", fontSign);
            paragraph.add(new Chunk(glue));
            Chunk principal = new Chunk("Chief Executive Officer/President", fontSign);
            paragraph.add(principal);
            document.add(paragraph);

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream getChildrensDepositAccountCloserPDFReport(List<ChildrensDeposit> childrensDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Account number", "Existing A/C number", "Customer Id","Name & address","Closed Date");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3,3,3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (ChildrensDeposit childrensDeposit : childrensDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(childrensDeposit.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(childrensDeposit.getExgAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(childrensDeposit.getMember().getCustomer().getId()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(childrensDeposit.getMember().getName() +"\n"+childrensDeposit.getMember().getMemberPersonalDetail().getResidentialAddress()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(childrensDeposit.getAccountClosedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Childrens Deposit List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream getDoubleSchemeCloserPDFReport(List<DoubleScheme> doubleSchemes) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Account number", "Name", "Customer Id","Name & address","Closed Date");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3,3,3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (DoubleScheme doubleScheme : doubleSchemes) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(doubleScheme.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(doubleScheme.getMember().getName()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(doubleScheme.getMember().getCustomer().getId()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(doubleScheme.getMember().getName() +"\n"+doubleScheme.getMember().getMemberPersonalDetail().getResidentialAddress()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(doubleScheme.getAccountClosedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Double Scheme Deposit List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream getRecurringDepositCloserPDFReport(List<RecurringDeposit> recurringDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Account number", "Name", "Customer Id","Name & address","Closed Date");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3,3,3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (RecurringDeposit recurringDeposit : recurringDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(recurringDeposit.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(recurringDeposit.getMember().getName()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(recurringDeposit.getMember().getCustomer().getId()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(recurringDeposit.getMember().getName() +"\n"+recurringDeposit.getMember().getMemberPersonalDetail().getResidentialAddress()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(recurringDeposit.getAccountClosedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Recurring Deposit List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream getTermDepositCloserPDFReport(List<TermDeposit> termDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Account number", "Name", "Customer Id","Name & address","Closed Date");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3,3,3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (TermDeposit termDeposit : termDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(termDeposit.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(termDeposit.getMember().getName()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(termDeposit.getMember().getCustomer().getId()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(termDeposit.getMember().getName() +"\n"+termDeposit.getMember().getMemberPersonalDetail().getResidentialAddress()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(termDeposit.getAccountClosedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Term Deposit List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream getFixedDepositCloserPDFReport(List<FixedDeposit> fixedDeposits ) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Account number", "Name", "Customer Id","Name & address","Closed Date");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3,3,3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (FixedDeposit fixedDeposit : fixedDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getMember().getName()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getMember().getCustomer().getId()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(fixedDeposit.getMember().getName() +"\n"+fixedDeposit.getMember().getMemberPersonalDetail().getResidentialAddress()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(fixedDeposit.getAccountClosedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Fixed Deposit List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream getSavingBankCloserPDFReport(List<SavingsBankDeposit> savingsBankDeposits) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Account number", "Name", "Customer Id","Name & address","Closed Date");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3,3,3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (SavingsBankDeposit savingsBankDeposit : savingsBankDeposits) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getMember().getName()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getMember().getCustomer().getId()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(savingsBankDeposit.getMember().getName() +"\n"+savingsBankDeposit.getMember().getMemberPersonalDetail().getResidentialAddress()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(savingsBankDeposit.getAccountClosedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Saving Bank List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream getCurrentAccountCloserPDFReport(List<CurrentAccount> currentAccounts) throws IOException, ParseException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Account number", "Name", "Customer Id","Name & address","Closed Date");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 2, 3,3,3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (CurrentAccount currentAccount: currentAccounts) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getAccountNumber()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getName()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getCustomer().getId()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(currentAccount.getMember().getName() +"\n"+currentAccount.getMember().getMemberPersonalDetail().getResidentialAddress()), font));
                cell.setPaddingLeft(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                String getDateWithFormat = DateUtil.convertToDateString(currentAccount.getAccountClosedOn());

                cell = new PdfPCell(new Phrase(getDateWithFormat, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);
            }
            PdfWriter.getInstance(document, out);
            document.open();

            Chunk glueDate = new Chunk(new VerticalPositionMark());
            Paragraph paragraphDate;
            Font dateTime = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            paragraphDate = new Paragraph("Run Date:" + DateUtil.getTodayDateAndTime(), dateTime);
            paragraphDate.add(new Chunk(glueDate));
            Chunk reportType = new Chunk("Report Type:", dateTime);
            paragraphDate.add(reportType);
            document.add(paragraphDate);

            Paragraph para = new Paragraph();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.UNDERLINE);
            para = new Paragraph("Current Account List", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph(" ", fontHeader);
            document.add(para);
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

}


