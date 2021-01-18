package com.dfq.coeffi.cbs.report.audit;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class CreditDebitReportApi extends BaseController {

    private List<CreditDebitDto> creditDebitReport;
    BigDecimal totalCredit = new BigDecimal(0);
    BigDecimal totalDebit = new BigDecimal(0);

    private final AccountHeadService accountHeadService;
    private final TransactionService transactionService;

    @Autowired
    public CreditDebitReportApi(final AccountHeadService accountHeadService, final TransactionService transactionService) {
        this.accountHeadService = accountHeadService;
        this.transactionService = transactionService;
    }

    @PostMapping("/audit/credit-debit")
    public ResponseEntity<List<CreditDebitDto>> getCreditDebitAudit(@RequestBody CreditDebitDto inputDto) {
        List<CreditDebitDto> creditDebitDto = new ArrayList<>();
        creditDebitReport = creditDebitDto;
        List<AccountHead> accountHeads = accountHeadService.getAccountHeads();

        /*List<Transaction> transactions = transactionService.getAllTransactions();*/
        List<Transaction> transactions = transactionService.getAllTransactions(inputDto.getStartDate(),inputDto.getEndDate());
        if (transactions != null && transactions.size() > 0) {
            if (accountHeads != null && accountHeads.size() > 0) {
                for (AccountHead accountHead : accountHeads) {
                    BigDecimal accountHeadTotal = new BigDecimal(0);

                    CreditDebitDto creditDebitDto1 = new CreditDebitDto();
                    creditDebitDto1.setAccountHead(accountHead.getName());
                    creditDebitDto1.setAccountCode(accountHead.getId());

                    List<HeadDto> headDto = new ArrayList<>();
                    List<Transaction> filteredTransactions = transactions.stream()
                            .filter(transaction -> transaction.getAccountHead() != null && transaction.getAccountHead().getId() == accountHead.getId()).collect(Collectors.toList());

                    if (filteredTransactions != null && filteredTransactions.size() > 0) {
                        for (Transaction tr : filteredTransactions) {
                            HeadDto dto = new HeadDto();
                            dto.setAccountHead(tr.getAccountHead());
                            if (tr.getCreditAmount().intValue() > 0) {
                                dto.setAmount(tr.getCreditAmount());
                                accountHeadTotal = accountHeadTotal.add(tr.getCreditAmount());
                            } else if (tr.getDebitAmount().intValue() > 0) {
                                dto.setAmount(tr.getDebitAmount());
                                accountHeadTotal = accountHeadTotal.add(tr.getDebitAmount());
                            }
                            dto.setTransactionType(tr.getTransactionType());
                            dto.setNarration(tr.getRemark());
                            headDto.add(dto);
                        }
                    }
                    creditDebitDto1.setSubTotal(accountHeadTotal);
                    creditDebitDto1.setHeads(headDto);
                    creditDebitDto.add(creditDebitDto1);
                }
            }
            this.creditDebitReport = creditDebitDto;
        }
        return new ResponseEntity<>(creditDebitDto, HttpStatus.OK);
    }

    @GetMapping("/audit/credit-debit-report")
    public ResponseEntity<List<CreditDebitDto>> getCreditDebitAuditReport() {
        List<CreditDebitDto> creditDebitDto = new ArrayList<>();
        creditDebitReport = creditDebitDto;
        List<AccountHead> accountHeads = accountHeadService.getAccountHeads();

//        List<Transaction> transactions = transactionService.getAllTransactions();
        if (accountHeads != null && accountHeads.size() > 0) {
            List<CreditDebitDto> dtos = new ArrayList<>();
            for (AccountHead accountHead : accountHeads) {

                CreditDebitDto creditDebitDtoObj = new CreditDebitDto();
                List<Ledger> ledgers = accountHeadService.getAccountHeadLedgers(accountHead);
                if (ledgers != null && ledgers.size() > 0) {
                    List<HeadDto> headDtos = new ArrayList<>();

                    for (Ledger ledger : ledgers) {
                        List<Transaction> ledgerTransactions = transactionService.getAllTransactions(ledger, "CREDIT");
                        if (ledgerTransactions != null && ledgerTransactions.size() > 0) {
                            HeadDto headDto = new HeadDto();
                            headDto.setLedgerTransactions(ledgerTransactions);
                            headDto.setLedger(ledger);
                            headDto.setTransactionType("CREDIT");
                            BigDecimal totalLedger = ledgerTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
                            headDto.setAmount(totalLedger);
                            headDtos.add(headDto);

                        }
                    }

                    if (headDtos != null && headDtos.size() > 0) {
                        BigDecimal totalAccountHead = headDtos.stream().map(HeadDto::getAmount).reduce(BigDecimal::add).get();
                        creditDebitDtoObj.setSubTotal(totalAccountHead);
                        creditDebitDtoObj.setAccountHead(accountHead.getName());
                        creditDebitDtoObj.setHeads(headDtos);
                        creditDebitDto.add(creditDebitDtoObj);
                    }


                }


//                    BigDecimal accountHeadTotal = new BigDecimal(0);
//
//                    CreditDebitDto creditDebitDto1 = new CreditDebitDto();
//                    creditDebitDto1.setAccountHead(accountHead.getName());
//                    creditDebitDto1.setAccountCode(accountHead.getId());
//
//                    List<HeadDto> headDto = new ArrayList<>();
//                    List<Transaction> filteredTransactions = transactions.stream()
//                            .filter(transaction -> transaction.getAccountHead() != null && transaction.getAccountHead().getId() == accountHead.getId()).collect(Collectors.toList());
//
//                    if (filteredTransactions != null && filteredTransactions.size() > 0) {
//                        for (Transaction tr : filteredTransactions) {
//                            HeadDto dto = new HeadDto();
//                            dto.setAccountHead(tr.getAccountHead());
//                            if (tr.getCreditAmount().intValue() > 0) {
//                                dto.setAmount(tr.getCreditAmount());
//                                accountHeadTotal = accountHeadTotal.add(tr.getCreditAmount());
//                            } else if (tr.getDebitAmount().intValue() > 0) {
//                                dto.setAmount(tr.getDebitAmount());
//                                accountHeadTotal = accountHeadTotal.add(tr.getDebitAmount());
//                            }
//                            dto.setTransactionType(tr.getTransactionType());
//                            dto.setNarration(tr.getRemark());
//                            headDto.add(dto);
//                        }
//                    }
//                    creditDebitDto1.setSubTotal(accountHeadTotal);
//                    creditDebitDto1.setHeads(headDto);
//                    creditDebitDto.add(creditDebitDto1);
            }
        }
        this.creditDebitReport = creditDebitDto;
        return new ResponseEntity<>(creditDebitDto, HttpStatus.OK);
    }

    @GetMapping("/audit/debit-report")
    public ResponseEntity<List<CreditDebitDto>> getDebitAuditReport() {
        List<CreditDebitDto> creditDebitDto = new ArrayList<>();
        creditDebitReport = creditDebitDto;
        List<AccountHead> accountHeads = accountHeadService.getAccountHeads();

        if (accountHeads != null && accountHeads.size() > 0) {
            List<CreditDebitDto> dtos = new ArrayList<>();
            for (AccountHead accountHead : accountHeads) {

                CreditDebitDto creditDebitDtoObj = new CreditDebitDto();
                List<Ledger> ledgers = accountHeadService.getAccountHeadLedgers(accountHead);
                if (ledgers != null && ledgers.size() > 0) {
                    List<HeadDto> headDtos = new ArrayList<>();

                    for (Ledger ledger : ledgers) {
                        List<Transaction> ledgerTransactions = transactionService.getAllTransactions(ledger, "DEBIT");
                        if (ledgerTransactions != null && ledgerTransactions.size() > 0) {
                            HeadDto headDto = new HeadDto();
                            headDto.setLedgerTransactions(ledgerTransactions);
                            headDto.setLedger(ledger);
                            headDto.setTransactionType("DEBIT");
                            BigDecimal totalLedger = ledgerTransactions.stream().map(Transaction::getDebitAmount).reduce(BigDecimal::add).get();
                            headDto.setAmount(totalLedger);
                            headDtos.add(headDto);

                        }
                    }

                    if (headDtos != null && headDtos.size() > 0) {
                        BigDecimal totalAccountHead = headDtos.stream().map(HeadDto::getAmount).reduce(BigDecimal::add).get();
                        creditDebitDtoObj.setSubTotal(totalAccountHead);
                        creditDebitDtoObj.setAccountHead(accountHead.getName());
                        creditDebitDtoObj.setHeads(headDtos);
                        creditDebitDto.add(creditDebitDtoObj);
                    }
                }
            }
        }
        return new ResponseEntity<>(creditDebitDto, HttpStatus.OK);
    }

    @GetMapping("/audit/credit-debit/excel")
    public ResponseEntity<List<Transaction>> auditCreditDebitReport(HttpServletResponse response) throws ServletException, IOException {

        totalDebit = new BigDecimal(0);
        totalCredit = new BigDecimal(0);

        OutputStream out = null;
        try {

            String fileName = "Transaction report";
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");

            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet workSheet = w.createSheet("Transaction", 0);


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


            workSheet.addCell(new Label(0, 2, "No.", headerFormat));
            workSheet.addCell(new Label(1, 2, "Credit Details", headerFormat));
            workSheet.addCell(new Label(2, 2, "Amount", headerFormat));
            workSheet.addCell(new Label(3, 2, "Amount", headerFormat));

            workSheet.addCell(new Label(4, 2, "No.", headerFormat));
            workSheet.addCell(new Label(5, 2, "Debit Details", headerFormat));
            workSheet.addCell(new Label(6, 2, "Amount", headerFormat));
            workSheet.addCell(new Label(7, 2, "Amount", headerFormat));


            if (creditDebitReport != null) {
                List<HeadDto> dtos = new ArrayList<>();
                List<HeadDto> creditDto = new ArrayList<>();
                List<HeadDto> debitDto = new ArrayList<>();


                // For Credit Print
                int rowCount = 3;

                if (creditDebitReport != null && creditDebitReport.size() > 0) {
                    for (int i = 0; i < creditDebitReport.size(); i++) {
                        if (creditDebitReport.get(i).getHeads() != null && creditDebitReport.get(i).getHeads().size() > 0) {

                            workSheet.addCell(new Label(0, rowCount, "" + (i + 1), totalFormat));
                            workSheet.addCell(new Label(1, rowCount, "" + creditDebitReport.get(i).getAccountHead(), totalFormat));
                            workSheet.addCell(new Label(2, rowCount, "", totalFormat));
                            workSheet.addCell(new Label(3, rowCount, "" + getCreditTotal(creditDebitReport.get(i).getAccountCode()), totalFormat));

                            rowCount++;
                        }

                        if (creditDebitReport.get(i).getHeads() != null && creditDebitReport.get(i).getHeads().size() > 0) {
                            for (HeadDto dto : creditDebitReport.get(i).getHeads()) {
                                if (dto.getTransactionType().equalsIgnoreCase("CREDIT")) {

                                    workSheet.addCell(new Label(0, rowCount, ""));
                                    workSheet.addCell(new Label(1, rowCount, "" + dto.getNarration()));
                                    workSheet.addCell(new Label(2, rowCount, "" + dto.getAmount()));
                                    workSheet.addCell(new Label(3, rowCount, ""));
                                    rowCount++;
                                }
                            }
                        }
                    }
                }
                rowCount = 3;
                if (creditDebitReport != null && creditDebitReport.size() > 0) {
                    for (int i = 0; i < creditDebitReport.size(); i++) {

                        if (creditDebitReport.get(i).getHeads() != null && creditDebitReport.get(i).getHeads().size() > 0) {
                            workSheet.addCell(new Label(4, rowCount, "" + (i + 1), totalFormat));
                            workSheet.addCell(new Label(5, rowCount, "" + creditDebitReport.get(i).getAccountHead(), totalFormat));
                            workSheet.addCell(new Label(6, rowCount, "", totalFormat));
                            workSheet.addCell(new Label(7, rowCount, "" + getDebitTotal(creditDebitReport.get(i).getAccountCode()), totalFormat));

                            rowCount++;
                        }

                        if (creditDebitReport.get(i).getHeads() != null && creditDebitReport.get(i).getHeads().size() > 0) {
                            for (HeadDto dto : creditDebitReport.get(i).getHeads()) {
                                if (dto.getTransactionType().equalsIgnoreCase("DEBIT")) {

                                    workSheet.addCell(new Label(4, rowCount, ""));
                                    workSheet.addCell(new Label(5, rowCount, "" + dto.getNarration()));
                                    workSheet.addCell(new Label(6, rowCount, "" + dto.getAmount()));
                                    workSheet.addCell(new Label(7, rowCount, ""));
                                    rowCount++;
                                }
                            }
                        }
                    }
                }


                int count = workSheet.getRows();
                workSheet.addCell(new Label(0, count, "Credit Sub Total", totalFormat));
                workSheet.addCell(new Label(1, count, "", totalFormat));
                workSheet.addCell(new Label(2, count, "", totalFormat));
                workSheet.addCell(new Label(3, count, "" + totalCredit, totalFormat));

                workSheet.addCell(new Label(4, count, "Debit Sub Total", totalFormat));
                workSheet.addCell(new Label(5, count, "", totalFormat));
                workSheet.addCell(new Label(6, count, "", totalFormat));
                workSheet.addCell(new Label(7, count, "" + totalDebit, totalFormat));

                count++;

                workSheet.addCell(new Label(0, count, "Grand Total", totalFormat));
                workSheet.addCell(new Label(1, count, "", totalFormat));
                workSheet.addCell(new Label(2, count, "", totalFormat));
                workSheet.addCell(new Label(3, count, "", totalFormat));
                workSheet.addCell(new Label(4, count, "", totalFormat));
                workSheet.addCell(new Label(5, count, "", totalFormat));
                workSheet.addCell(new Label(6, count, "", totalFormat));
                workSheet.addCell(new Label(7, count, "" + (totalCredit.add(totalDebit)), totalFormat));

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

    private BigDecimal getCreditTotal(long code) {
        BigDecimal total = new BigDecimal(0);
        if (creditDebitReport != null) {
            for (CreditDebitDto creditDebitDto : creditDebitReport) {
                if (creditDebitDto.getHeads() != null && creditDebitDto.getHeads().size() > 0) {
                    for (HeadDto dto : creditDebitDto.getHeads()) {
                        if (dto.getAmount() != null && dto.getAccountHead().getId() == code && dto.getTransactionType().equalsIgnoreCase("CREDIT")) {
                            total = total.add(dto.getAmount());

                        }
                    }
                }
            }
        }
        totalCredit = totalCredit.add(total);
        return total;
    }

    private BigDecimal getDebitTotal(long code) {
        BigDecimal total = new BigDecimal(0);
        if (creditDebitReport != null) {
            for (CreditDebitDto creditDebitDto : creditDebitReport) {
                if (creditDebitDto.getHeads() != null && creditDebitDto.getHeads().size() > 0) {
                    for (HeadDto dto : creditDebitDto.getHeads()) {
                        if (dto.getAmount() != null && dto.getAccountHead().getId() == code && dto.getTransactionType().equalsIgnoreCase("DEBIT")) {
                            total = total.add(dto.getAmount());

                        }
                    }
                }
            }
        }
        totalDebit = totalDebit.add(total);
        return total;
    }
}