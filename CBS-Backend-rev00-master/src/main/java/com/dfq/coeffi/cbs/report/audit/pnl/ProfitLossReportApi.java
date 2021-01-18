package com.dfq.coeffi.cbs.report.audit.pnl;

import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.deposit.entity.RecurringDeposit;
import com.dfq.coeffi.cbs.deposit.service.FixedDepositService;
import com.dfq.coeffi.cbs.deposit.service.PigmyDepositService;
import com.dfq.coeffi.cbs.deposit.service.RecurringDepositService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.report.audit.CreditDebitDto;
import com.dfq.coeffi.cbs.report.audit.HeadDto;
import com.dfq.coeffi.cbs.report.audit.ProfitLossDto;
import com.dfq.coeffi.cbs.transaction.entity.BankTransaction;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.BankTransactionService;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ProfitLossReportApi extends BaseController {

    private List<CreditDebitDto> creditDebitReport;
    BigDecimal totalProfit = new BigDecimal(0);
    BigDecimal totalLoss = new BigDecimal(0);

    private final AccountHeadService accountHeadService;
    private final TransactionService transactionService;
    private final FixedDepositService fixedDepositService;
    private final RecurringDepositService recurringDepositService;
    private final PigmyDepositService pigmyDepositService;
    private final BankTransactionService bankTransactionService;

    @Autowired
    public ProfitLossReportApi(final AccountHeadService accountHeadService, final TransactionService transactionService,
                               final FixedDepositService fixedDepositService,
                               final RecurringDepositService recurringDepositService,
                               final PigmyDepositService pigmyDepositService,
                               final BankTransactionService bankTransactionService) {
        this.accountHeadService = accountHeadService;
        this.transactionService = transactionService;
        this.fixedDepositService = fixedDepositService;
        this.recurringDepositService = recurringDepositService;
        this.pigmyDepositService = pigmyDepositService;
        this.bankTransactionService = bankTransactionService;
    }

    @PostMapping("/audit/profit-loss-report")
    public ResponseEntity<Map<String, List<ProfitLossDto>>> getProfitAndLossReport(@RequestBody ProfitLossDto inputProfitDto) {

        Map<String, List<ProfitLossDto>> returnObject = new HashMap<>();
        AccountHead expenseAccountHead = accountHeadService.findByName("Expense");
        List<Ledger> ledgers = accountHeadService.getAccountHeadLedgers(expenseAccountHead);
        List<ProfitLossDto> profitLossDto = new ArrayList<>();
        if (ledgers != null && ledgers.size() > 0) {
            for (Ledger ledger : ledgers) {
                List<Transaction> lossTransactions = transactionService.getAllTransactions(inputProfitDto.getStartDate(),inputProfitDto.getEndDate(),ledger);
                if (lossTransactions != null && lossTransactions.size() > 0) {
                    ProfitLossDto dto = new ProfitLossDto();
                    dto.setLedger(ledger);
                    dto.setAccountHead(expenseAccountHead);
                    dto.setLossTransactions(lossTransactions);

                    BigDecimal totalLedger = lossTransactions.stream().map(Transaction::getDebitAmount).reduce(BigDecimal::add).get();
                    dto.setTotalAmount(totalLedger);
                    profitLossDto.add(dto);
                }
            }
            returnObject.put("Expense", profitLossDto);
        }

        // Deposit Interest Losses
        List<ProfitLossDto> depositLossDto = new ArrayList<>();

        List<FixedDeposit> fixedDeposits = fixedDepositService.getAllFixedDeposits(inputProfitDto.getStartDate(),inputProfitDto.getEndDate(),true);
        if (fixedDeposits != null && fixedDeposits.size() > 0) {
            ProfitLossDto dto = new ProfitLossDto();
            BigDecimal totalFixedDepositInterest = fixedDeposits.stream().map(FixedDeposit::getInterestAmount).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalFixedDepositInterest);
            dto.setLedgerName("Fixed Deposit");
            depositLossDto.add(dto);
        }
        returnObject.put("DepositInterest", depositLossDto);

        List<RecurringDeposit>  recurringDeposits= recurringDepositService.getAllRecurringDeposit(inputProfitDto.getStartDate(),inputProfitDto.getEndDate(),true);
        if (recurringDeposits != null && recurringDeposits.size() > 0) {
            ProfitLossDto dto = new ProfitLossDto();
            BigDecimal totalRecurringDepositInterest = recurringDeposits.stream().map(RecurringDeposit::getInterestAmount).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalRecurringDepositInterest);
            dto.setLedgerName("Recurring Deposit");
            depositLossDto.add(dto);
        }
        returnObject.put("DepositInterest", depositLossDto);

        List<PigmyDeposit>  pigmyDeposits = pigmyDepositService.getAllPigmyDeposits(inputProfitDto.getStartDate(),inputProfitDto.getEndDate(),true);
        if (pigmyDeposits != null && pigmyDeposits.size() > 0) {
            ProfitLossDto dto = new ProfitLossDto();
            BigDecimal totalPigmyDepositInterest = pigmyDeposits.stream().map(PigmyDeposit::getCalculatedInterest).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalPigmyDepositInterest);
            dto.setLedgerName("Pigmy Deposit");
            depositLossDto.add(dto);
        }
        returnObject.put("DepositInterest", depositLossDto);

        Ledger shareLedger = accountHeadService.getLedgerByName("Share Fees");
        List<Transaction> profitShareFeeTransactions = transactionService.getAllTransactions(inputProfitDto.getStartDate(),inputProfitDto.getEndDate(),shareLedger);
        if (profitShareFeeTransactions != null && profitShareFeeTransactions.size() > 0) {
            List<ProfitLossDto> shareFeeProfitDto = new ArrayList<>();

            ProfitLossDto dto = new ProfitLossDto();
            dto.setProfitTransactions(profitShareFeeTransactions);
            dto.setAccountHead(shareLedger.getAccountHead());
            dto.setLedger(shareLedger);

            BigDecimal totalLedger = profitShareFeeTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalLedger);
            shareFeeProfitDto.add(dto);

            returnObject.put("ShareFees", shareFeeProfitDto);
        }

        Ledger shareOtherLedger = accountHeadService.getLedgerByName("Other Fees");
        List<Transaction> profitShareOtherFeeTransactions = transactionService.getAllTransactions(inputProfitDto.getStartDate(),inputProfitDto.getEndDate(),shareOtherLedger);
        if (profitShareOtherFeeTransactions != null && profitShareOtherFeeTransactions.size() > 0) {
            List<ProfitLossDto> shareFeeProfitDto = new ArrayList<>();

            ProfitLossDto dto = new ProfitLossDto();
            dto.setProfitTransactions(profitShareOtherFeeTransactions);
            dto.setAccountHead(shareOtherLedger.getAccountHead());
            dto.setLedger(shareOtherLedger);

            BigDecimal totalLedger = profitShareOtherFeeTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalLedger);
            shareFeeProfitDto.add(dto);

            returnObject.put("ShareOtherFees", shareFeeProfitDto);
        }

        Ledger shareEntryFeeLedger = accountHeadService.getLedgerByName("Entry Fees");
        List<Transaction> profitShareEntryFeeTransactions = transactionService.getAllTransactions(inputProfitDto.getStartDate(),inputProfitDto.getEndDate(),shareEntryFeeLedger);
        if (profitShareEntryFeeTransactions != null && profitShareEntryFeeTransactions.size() > 0) {
            List<ProfitLossDto> shareFeeProfitDto = new ArrayList<>();

            ProfitLossDto dto = new ProfitLossDto();
            dto.setProfitTransactions(profitShareEntryFeeTransactions);
            dto.setAccountHead(shareEntryFeeLedger.getAccountHead());
            dto.setLedger(shareEntryFeeLedger);

            BigDecimal totalLedger = profitShareEntryFeeTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalLedger);
            shareFeeProfitDto.add(dto);

            returnObject.put("ShareEntryFees", shareFeeProfitDto);
        }

        Ledger loanInterestChargeLedger = accountHeadService.getLedgerByName("Loan Interest");
        List<Transaction> profitLoanInterestTransactions = transactionService.getAllTransactions(inputProfitDto.getStartDate(),inputProfitDto.getEndDate(),loanInterestChargeLedger);
        if (profitLoanInterestTransactions != null && profitLoanInterestTransactions.size() > 0) {
            List<ProfitLossDto> shareFeeProfitDto = new ArrayList<>();
            ProfitLossDto dto = new ProfitLossDto();
            dto.setProfitTransactions(profitLoanInterestTransactions);
            dto.setLedger(loanInterestChargeLedger);
            BigDecimal totalLedger = profitLoanInterestTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalLedger);
            shareFeeProfitDto.add(dto);

            returnObject.put("LoanInterestCharges", shareFeeProfitDto);
        }

        Ledger loanServiceChargeLedger = accountHeadService.getLedgerByName("Loan Service Charges");
        List<Transaction> profitLoanServiceTransactions = transactionService.getAllTransactions(inputProfitDto.getStartDate(),inputProfitDto.getEndDate(),loanServiceChargeLedger);
        if (profitLoanServiceTransactions != null && profitLoanServiceTransactions.size() > 0) {
            List<ProfitLossDto> shareFeeProfitDto = new ArrayList<>();
            ProfitLossDto dto = new ProfitLossDto();
            dto.setProfitTransactions(profitLoanServiceTransactions);
            dto.setLedger(loanServiceChargeLedger);
            BigDecimal totalLedger = profitLoanServiceTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalLedger);
            shareFeeProfitDto.add(dto);

            returnObject.put("LoanServiceCharges", shareFeeProfitDto);

        }

        List<BankTransaction> externalBankTransaction = bankTransactionService.getBankTransactionsByBetweenDates(inputProfitDto.getStartDate(),inputProfitDto.getEndDate());
        if (externalBankTransaction != null && externalBankTransaction.size() > 0) {
            List<ProfitLossDto> externalBankTransactionDto = new ArrayList<>();
            ProfitLossDto dto = new ProfitLossDto();
            /*List<BankTransaction> filteredTransaction = externalBankTransaction.stream().filter(x -> "INTEREST".equals(x.getTransactionType())).collect(Collectors.toList());
            dto.setExternalBankTransactions(filteredTransaction);
            BigDecimal totalLedger = externalBankTransaction.stream().filter(x -> "INTEREST".equals(x.getTransactionType())).map(BankTransaction::getCreditAmount).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalLedger);
            Collections.reverse(filteredTransaction);
            dto.setTotalAmount(totalLedger);
            externalBankTransactionDto.add(dto);*/
            Collections.reverse(externalBankTransaction);
            dto.setTotalAmount(externalBankTransaction.get(0).getBalance());
            externalBankTransactionDto.add(dto);
            returnObject.put("AssetExternalBankTransaction", externalBankTransactionDto);
        }

        /*List<BankTransaction> externalBankTransactions = bankTransactionService.getBankTransactionsByBetweenDates(inputProfitDto.getStartDate(),inputProfitDto.getEndDate());
        if (externalBankTransactions != null && externalBankTransactions.size() > 0) {
            List<ProfitLossDto> externalBankTransactionDto = new ArrayList<>();
            ProfitLossDto dto = new ProfitLossDto();
            Collections.reverse(externalBankTransactions);
            dto.setTotalAmount(externalBankTransactions.get(0).getBalance());
            externalBankTransactionDto.add(dto);

            returnObject.put("ExternalBankBalance", externalBankTransactionDto);
        }*/

        return new ResponseEntity<>(returnObject, HttpStatus.OK);
    }

    @GetMapping("/audit/profit-loss/excel")
    public ResponseEntity<List<Transaction>> profitAndLossExcelReport(HttpServletResponse response) throws ServletException, IOException {

        totalProfit = new BigDecimal(0);
        totalLoss = new BigDecimal(0);

        OutputStream out = null;
        try {

            String fileName = "Profit & Loss Report";
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");

            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet workSheet = w.createSheet("ProfitAndLoss", 0);


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


            workSheet.addCell(new Label(0, 2, "Loss Details", headerFormat));
            workSheet.addCell(new Label(1, 2, "Amount", headerFormat));
            workSheet.addCell(new Label(2, 2, "Amount", headerFormat));

            workSheet.addCell(new Label(4, 2, "Profit Details", headerFormat));
            workSheet.addCell(new Label(5, 2, "Amount", headerFormat));
            workSheet.addCell(new Label(6, 2, "Amount", headerFormat));


            if (creditDebitReport != null) {

                // For Credit Print
                int rowCount = 3;

                if (creditDebitReport != null && creditDebitReport.size() > 0) {
                    for (int i = 0; i < creditDebitReport.size(); i++) {
                        if (creditDebitReport.get(i).getHeads() != null && creditDebitReport.get(i).getHeads().size() > 0) {

                            workSheet.addCell(new Label(0, rowCount, "" + creditDebitReport.get(i).getAccountHead(), totalFormat));
                            workSheet.addCell(new Label(1, rowCount, "", totalFormat));
                            workSheet.addCell(new Label(2, rowCount, "" + calculateProfitTotal(creditDebitReport.get(i).getAccountCode()), totalFormat));

                            rowCount++;
                        }

                        if (creditDebitReport.get(i).getHeads() != null && creditDebitReport.get(i).getHeads().size() > 0) {
                            for (HeadDto dto : creditDebitReport.get(i).getHeads()) {
                                if (dto.getTransactionType().equalsIgnoreCase("CREDIT")) {

                                    workSheet.addCell(new Label(0, rowCount, "" + dto.getNarration()));
                                    workSheet.addCell(new Label(1, rowCount, "" + dto.getAmount()));
                                    workSheet.addCell(new Label(2, rowCount, ""));
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
                            workSheet.addCell(new Label(4, rowCount, "" + creditDebitReport.get(i).getAccountHead(), totalFormat));
                            workSheet.addCell(new Label(5, rowCount, "", totalFormat));
                            workSheet.addCell(new Label(6, rowCount, "" + calculateLossTotal(creditDebitReport.get(i).getAccountCode()), totalFormat));

                            rowCount++;
                        }

                        if (creditDebitReport.get(i).getHeads() != null && creditDebitReport.get(i).getHeads().size() > 0) {
                            for (HeadDto dto : creditDebitReport.get(i).getHeads()) {
                                if (dto.getTransactionType().equalsIgnoreCase("DEBIT")) {

                                    workSheet.addCell(new Label(4, rowCount, "" + dto.getNarration()));
                                    workSheet.addCell(new Label(5, rowCount, "" + dto.getAmount()));
                                    workSheet.addCell(new Label(6, rowCount, ""));
                                    rowCount++;
                                }
                            }
                        }
                    }
                }


                int count = workSheet.getRows();
                workSheet.addCell(new Label(0, count, "Total", totalFormat));
                workSheet.addCell(new Label(1, count, "", totalFormat));
                workSheet.addCell(new Label(2, count, "", totalFormat));
                workSheet.addCell(new Label(3, count, "" + totalProfit, totalFormat));

                workSheet.addCell(new Label(4, count, "Total", totalFormat));
                workSheet.addCell(new Label(5, count, "", totalFormat));
                workSheet.addCell(new Label(6, count, "", totalFormat));
                workSheet.addCell(new Label(7, count, "" + totalLoss, totalFormat));

            }

            w.write();
            w.close();

        } catch (Exception e) {
            throw new ServletException("Exception in profit & loss excel report download", e);
        } finally {
            if (out != null)
                out.close();
        }


        return new ResponseEntity<>(HttpStatus.OK);

    }

    private BigDecimal calculateProfitTotal(long code) {
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
        totalProfit = totalProfit.add(total);
        return total;
    }

    private BigDecimal calculateLossTotal(long code) {
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
        totalLoss = totalLoss.add(total);
        return total;
    }
}