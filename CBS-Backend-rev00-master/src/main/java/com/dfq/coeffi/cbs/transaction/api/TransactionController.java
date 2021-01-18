package com.dfq.coeffi.cbs.transaction.api;

import com.dfq.coeffi.cbs.admin.entity.BODDate;
import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.transaction.dto.TransactionDto;
import com.dfq.coeffi.cbs.transaction.entity.EodTransaction;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.utils.DateUtil;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.*;
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@RestController
@Slf4j
public class TransactionController extends BaseController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BODDateService bodDateService;

    @GetMapping("/transaction/account")
    public ResponseEntity<List<Transaction>> getTransactions() {

        Optional<BODDate> bodDateObj = bodDateService.getBODDateByStatus();
        if (!bodDateObj.isPresent()) {
            throw new EntityNotFoundException("Bod not started yet");
        }

        BODDate bodDate = bodDateObj.get();
        List<Transaction> transactions = transactionService.getAllTransactions(bodDate.getBodDate());
        if (CollectionUtils.isEmpty(transactions)) {
            throw new EntityNotFoundException("transactions not found");
        }
        Collections.reverse(transactions);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/transaction-excel-report")
    public ResponseEntity<List<Transaction>> excelTransactionReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Transaction> transactions = null;

        OutputStream out = null;
        try {
            String fileName = "Transaction report";
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");

            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Transaction", 0);

            // Logo logic
            double CELL_DEFAULT_HEIGHT = 8;
            double CELL_DEFAULT_WIDTH = 4;

            WritableCellFormat headerFormat = new WritableCellFormat();
            WritableFont font = new WritableFont(WritableFont.createFont("Ubuntu"), WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
            headerFormat.setBackground(Colour.SKY_BLUE);
            headerFormat.setFont(font);

            WritableCellFormat totalFormat = new WritableCellFormat();
            WritableFont totalFormatfont = new WritableFont(WritableFont.createFont("Ubuntu"), WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
            totalFormat.setBackground(Colour.GRAY_50);
            totalFormat.setFont(totalFormatfont);

            WritableCellFormat yellowFormat = new WritableCellFormat();
            WritableFont fontForYellowFormat = new WritableFont(WritableFont.createFont("Ubuntu"), WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.GRAY_80);
            yellowFormat.setBackground(Colour.YELLOW);
            yellowFormat.setFont(fontForYellowFormat);

            s.addCell(new Label(0, 2, "Transaction Date", headerFormat));
            s.addCell(new Label(1, 2, "Account Number", headerFormat));
            s.addCell(new Label(2, 2, "Account Head", headerFormat));
            s.addCell(new Label(3, 2, "Transaction Type", headerFormat));
            s.addCell(new Label(4, 2, "Credit Amount", headerFormat));
            s.addCell(new Label(5, 2, "Debit Amount", headerFormat));
            s.addCell(new Label(6, 2, "Balance", headerFormat));

            transactions = transactionService.getAllTransactions();

            if (transactions != null) {

                int rowCount = 3;
                for (Transaction transaction : transactions) {
                    s.addCell(new Label(0, rowCount, "" + transaction.getTransactionOn()));
                    s.addCell(new Label(1, rowCount, "" + transaction.getAccountNumber()));
                    if (transaction.getAccountHead() != null) {
                        s.addCell(new Label(2, rowCount, "" + transaction.getAccountHead().getName()));
                    } else {
                        s.addCell(new Label(2, rowCount, "NA"));
                    }
                    s.addCell(new Label(3, rowCount, "" + transaction.getTransactionType()));
                    s.addCell(new Label(4, rowCount, "" + transaction.getCreditAmount()));
                    s.addCell(new Label(5, rowCount, "" + transaction.getDebitAmount()));
                    s.addCell(new Label(6, rowCount, "" + transaction.getBalance()));

                    rowCount++;
                }
            }
            w.write();
            w.close();

        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/bod-transaction")
    public ResponseEntity<EodTransaction> getBodTransaction() {

        EodTransaction eodTransaction = new EodTransaction();

        BigDecimal creditAmount = new BigDecimal(0);
        BigDecimal debitAmount = new BigDecimal(0);
        BigDecimal balanceAsOn = new BigDecimal(0);
        BigDecimal openingBalance = new BigDecimal(0);

        Optional<BODDate> bodDateObj = bodDateService.getBODDateByStatus();
        if (!bodDateObj.isPresent()) {
            throw new EntityNotFoundException("Bod not started yet");
        }

        List<Transaction> previousTransactions = transactionService.getAllTransactions(DateUtil.getTodayDate(), DateUtil.getSubtractedDate(6));
        if (previousTransactions != null && previousTransactions.size() > 0) {
            List<Transaction> todayTransactions = transactionService.getAllTransactions(DateUtil.getTodayDate());
            if (todayTransactions != null && todayTransactions.size() > 0) {
                for (Transaction transaction : todayTransactions) {
                    if (previousTransactions.contains(transaction)) {
                        previousTransactions.remove(transaction);
                    }
                }
            }
            if (previousTransactions != null && previousTransactions.size() > 0) {
                previousTransactions.sort(Comparator.comparing(Transaction::getId));
                openingBalance = previousTransactions.get(previousTransactions.size() - 1).getBalance();
            }
        } else {
            openingBalance = new BigDecimal(BigInteger.ZERO);
        }

        BODDate bodDate = bodDateObj.get();
        List<Transaction> transactions = transactionService.getAllTransactions(bodDate.getBodDate());
        List<Transaction> yesterdayTransactions = transactionService.getAllTransactions(DateUtil.getYesterdayDate());
        if (transactions != null && transactions.size() > 0) {
            for (Transaction transaction : transactions) {
                creditAmount = creditAmount.add(transaction.getCreditAmount());

                if(transaction.getOtherFees() != null && transaction.getOtherFees().intValue() > 0){
                    creditAmount = creditAmount.subtract(transaction.getOtherFees());
                }
                debitAmount = debitAmount.add(transaction.getDebitAmount());
            }
            Collections.reverse(transactions);
            balanceAsOn = transactions.get(0).getBalance();
        }

        eodTransaction.setOpeningBalance(balanceAsOn.add(debitAmount).subtract(creditAmount));
        eodTransaction.setCreditAmount(creditAmount);
        eodTransaction.setDebitAmount(debitAmount);
        eodTransaction.setBalanceAsOn(balanceAsOn);
        eodTransaction.setClosingBalance(balanceAsOn);
        return new ResponseEntity<>(eodTransaction, HttpStatus.OK);
    }

    @GetMapping("/transaction/transfer-type")
    public ResponseEntity<List<Transaction>> getTransactionsByTransferType() {
        List<Transaction> transactions = transactionService.findByTransferType("Transfer");
        if (CollectionUtils.isEmpty(transactions)) {
            throw new EntityNotFoundException("transactions not found");
        }
        Collections.reverse(transactions);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping("/transaction/by-type")
    public ResponseEntity<List<Transaction>> getTransactionsByCashType(@RequestBody TransactionDto transactionDto) {

        List<Transaction> transactions = null;
        if (transactionDto.getTransferType().equalsIgnoreCase("Cash")) {
            transactions = transactionService.findByTransferType("Cash");

            List<Transaction> transactionCashObj = new ArrayList<>();

            for (Transaction transaction : transactions) {
                if (transaction.getAccountHead().getName().equalsIgnoreCase("Loan") && transaction.getTransactionType().equalsIgnoreCase("CREDIT")) {
                    transactionCashObj.add(transaction);
                }
                // Removing loan debit entry from the cash in case of repayments
                else if (transaction.getAccountHead().getName().equalsIgnoreCase("Loan") && transaction.getTransactionType().equalsIgnoreCase("DEBIT")
                        && transaction.getParticulars() != null && transaction.getParticulars().equalsIgnoreCase("Repayment")) {
                    transactionCashObj.add(transaction);
                }
            }
            transactions.removeAll(transactionCashObj);

        } else if (transactionDto.getTransferType().equalsIgnoreCase("Bank")) {
            transactions = transactionService.findByTransferType("Bank");

            List<Transaction> transactionBankObj = new ArrayList<>();

            for (Transaction transaction : transactions) {
                if (transaction.getAccountHead().getName().equalsIgnoreCase("Loan") && transaction.getTransactionType().equalsIgnoreCase("CREDIT")) {
                    transactionBankObj.add(transaction);
                }
                // Removing loan debit entry from the cash in case of repayments
                else if (transaction.getAccountHead().getName().equalsIgnoreCase("Loan") && transaction.getTransactionType().equalsIgnoreCase("DEBIT")
                        && transaction.getParticulars() != null && transaction.getParticulars().equalsIgnoreCase("Repayment")) {
                    transactionBankObj.add(transaction);
                }
            }
            transactions.removeAll(transactionBankObj);
        }
        if (CollectionUtils.isEmpty(transactions)) {
            throw new EntityNotFoundException("transactions not found");
        }
        Collections.reverse(transactions);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping("/transaction/by-date-transfer-type")
    public ResponseEntity<List<Transaction>> getTransactionsByTransferTypeAndByDate(@RequestBody TransactionDto transactionDto) {
        String newYearDate = "01/04/2019";
        String startDate = "01/04/2019";
        String endDate = "01/04/2019";
        Date newYearD = DateUtil.convertToDate(newYearDate);
        Date sDate = DateUtil.convertToDate(startDate);
        Date end = DateUtil.convertToDate(endDate);
        if(transactionDto.getDateFrom().before(newYearD) || transactionDto.getDateTo().before(newYearD)){
            transactionDto.setDateFrom(sDate);
            transactionDto.setDateTo(end);
        }
        List<Transaction> transactions = transactionService.getTransactionByDateAndTransferType(transactionDto.getDateFrom(), transactionDto.getDateTo(), "Transfer");
        /*if (CollectionUtils.isEmpty(transactions)) {
            throw new EntityNotFoundException("transactions not found");
        }*/
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

}