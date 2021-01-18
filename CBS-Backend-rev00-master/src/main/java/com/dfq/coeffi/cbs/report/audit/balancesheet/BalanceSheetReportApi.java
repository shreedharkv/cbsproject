package com.dfq.coeffi.cbs.report.audit.balancesheet;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.report.audit.BalanceSheetDto;
import com.dfq.coeffi.cbs.report.audit.CreditDebitDto;
import com.dfq.coeffi.cbs.report.audit.HeadDto;
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
public class BalanceSheetReportApi extends BaseController {

    private List<CreditDebitDto> creditDebitReport;
    BigDecimal totalLiability = new BigDecimal(0);
    BigDecimal totalAsset = new BigDecimal(0);

    private final AccountHeadService accountHeadService;
    private final TransactionService transactionService;
    private final BankTransactionService bankTransactionService;

    @Autowired
    public BalanceSheetReportApi(final AccountHeadService accountHeadService, final TransactionService transactionService,
                                 BankTransactionService bankTransactionService) {
        this.accountHeadService = accountHeadService;
        this.transactionService = transactionService;
        this.bankTransactionService = bankTransactionService;
    }

    @GetMapping("/audit/balance-sheet-report")
    public ResponseEntity<List<CreditDebitDto>> getBalanceSheetReport() {
        List<CreditDebitDto> creditDebitDto = new ArrayList<>();
        creditDebitReport = creditDebitDto;
        List<AccountHead> accountHeads = accountHeadService.getAccountHeads();

        List<Transaction> transactions = transactionService.getAllTransactions();
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

    @PostMapping("/audit/balance-sheet")
    public ResponseEntity<Map<String, List<BalanceSheetDto>>> getProfitAndLossReport(@RequestBody BalanceSheetDto inputBalanceSheetDto) {

        Map<String, List<BalanceSheetDto>> returnObject = new HashMap<>();

        AccountHead shareFeesAccountHead = accountHeadService.findByName("Share Capital");
        if (shareFeesAccountHead != null) {
            List<BalanceSheetDto> balanceSheetDtos = null;
            List<Ledger> ledgers = accountHeadService.getAccountHeadLedgers(shareFeesAccountHead);
            if (ledgers != null && ledgers.size() > 0) {
                balanceSheetDtos = new ArrayList<>();
                for (Ledger ledger : ledgers) {
                    if (!ledger.getName().equalsIgnoreCase("Share Capital")) {
                        System.out.println("share capital in");
                        BalanceSheetDto dto = new BalanceSheetDto();
                        List<Transaction> transactions = transactionService.getAllTransactions(inputBalanceSheetDto.getStartDate(),inputBalanceSheetDto.getEndDate(),ledger);
                        if (transactions != null && transactions.size() > 0) {
                            BigDecimal totalLedger = transactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
                            dto.setTotalAmount(totalLedger);
                            dto.setAssetTransactions(transactions);
                            dto.setLedger(ledger);
                            balanceSheetDtos.add(dto);
                        }
                    }
                }

            }
            returnObject.put("AssetShareFees", balanceSheetDtos);
        }

//
//        List<Transaction> shareFeeTransactions = transactionService.getAllTransactions(shareFeesAccountHead);
//        if (shareFeeTransactions != null && shareFeeTransactions.size() > 0) {
//            List<BalanceSheetDto> shareFeeProfitDto = new ArrayList<>();
//
//            BalanceSheetDto dto = new BalanceSheetDto();
//            dto.setAssetTransactions(shareFeeTransactions);
//            dto.setAccountHead(shareFeesAccountHead);
//
//            BigDecimal totalShareFee = shareFeeTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
//            dto.setTotalAmount(totalShareFee);
//            shareFeeProfitDto.add(dto);
//
//            returnObject.put("AssetShareFees", shareFeeProfitDto);
//        }

        AccountHead loanAccountHead = accountHeadService.findByName("Loan");
        if (shareFeesAccountHead != null) {
            List<BalanceSheetDto> balanceSheetLoanDtos = null;
            List<Ledger> ledgers = accountHeadService.getAccountHeadLedgers(loanAccountHead);
            if (ledgers != null && ledgers.size() > 0) {
                balanceSheetLoanDtos = new ArrayList<>();
                for (Ledger ledger : ledgers) {
                    BalanceSheetDto dto = new BalanceSheetDto();
                    List<Transaction> transactions = transactionService.getAllTransactions(inputBalanceSheetDto.getStartDate(),inputBalanceSheetDto.getEndDate(),ledger);
                    if (transactions != null && transactions.size() > 0) {
                        BigDecimal totalLedger = transactions.stream().map(Transaction::getDebitAmount).reduce(BigDecimal::add).get();
                        dto.setTotalAmount(totalLedger);
                        dto.setLiabilityTransactions(transactions);
                        dto.setLedger(ledger);
                        balanceSheetLoanDtos.add(dto);
                    }
                }

            }


            AccountHead cropLoanAccountHead = accountHeadService.findByName("Crop Loan");
            if (cropLoanAccountHead != null) {
                List<Ledger> cropLoanLedgers = accountHeadService.getAccountHeadLedgers(cropLoanAccountHead);
                if (cropLoanLedgers != null && cropLoanLedgers.size() > 0) {
                    for (Ledger ledger : cropLoanLedgers) {
                        System.out.println("Ledgers : " + ledger.getName());
                        BalanceSheetDto dto = new BalanceSheetDto();
                        List<Transaction> transactions = transactionService.getAllTransactions(inputBalanceSheetDto.getStartDate(),inputBalanceSheetDto.getEndDate(),ledger);
                        if (transactions != null && transactions.size() > 0) {
                            BigDecimal totalLedger = transactions.stream().map(Transaction::getDebitAmount).reduce(BigDecimal::add).get();
                            dto.setTotalAmount(totalLedger);
                            dto.setLiabilityTransactions(transactions);
                            dto.setLedger(ledger);
                            balanceSheetLoanDtos.add(dto);
                        }
                    }
                }
            }


            returnObject.put("LiabilityLoan", balanceSheetLoanDtos);
        }

//        AccountHead memberLoanAccountHead = accountHeadService.findByName("Loan");
//        List<Transaction> memberLoanTransactions = transactionService.getAllTransactions(shareFeesAccountHead);
//        if (memberLoanTransactions != null && memberLoanTransactions.size() > 0) {
//            List<BalanceSheetDto> shareFeeProfitDto = new ArrayList<>();
//
//            BalanceSheetDto dto = new BalanceSheetDto();
//            dto.setAssetTransactions(memberLoanTransactions);
//            dto.setAccountHead(memberLoanAccountHead);
//
//            BigDecimal totalShareFee = memberLoanTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
//            dto.setTotalAmount(totalShareFee);
//            shareFeeProfitDto.add(dto);
//
//            returnObject.put("AssetLoan", shareFeeProfitDto);
//        }

        Ledger loanInterestChargeLedger = accountHeadService.getLedgerByName("Loan Interest");
        List<Transaction> assetLoanInterestTransactions = transactionService.getAllTransactions(inputBalanceSheetDto.getStartDate(),inputBalanceSheetDto.getEndDate(),loanInterestChargeLedger);
        if (assetLoanInterestTransactions != null && assetLoanInterestTransactions.size() > 0) {
            List<BalanceSheetDto> shareFeeProfitDto = new ArrayList<>();
            BalanceSheetDto dto = new BalanceSheetDto();
            dto.setAssetTransactions(assetLoanInterestTransactions);
            dto.setLedger(loanInterestChargeLedger);
            BigDecimal totalLedger = assetLoanInterestTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalLedger);
            shareFeeProfitDto.add(dto);

            returnObject.put("AssetLoanInterestCharges", shareFeeProfitDto);
        }

        List<BankTransaction> externalBankTransaction = bankTransactionService.getBankTransactionsByBetweenDates(inputBalanceSheetDto.getStartDate(),inputBalanceSheetDto.getEndDate());
        if (externalBankTransaction != null && externalBankTransaction.size() > 0) {
            List<BalanceSheetDto> externalBankTransactionDto = new ArrayList<>();
            BalanceSheetDto dto = new BalanceSheetDto();
            List<BankTransaction> filteredTransaction = externalBankTransaction.stream().filter(item -> item.getBalance().intValue() > 0).collect(Collectors.toList());
            dto.setExternalBankTransactions(filteredTransaction);
  //          BigDecimal totalLedger = externalBankTransaction.stream().map(BankTransaction::getBalance).reduce(BigDecimal::add).get();
 //            dto.setTotalAmount(totalLedger);
            Collections.reverse(filteredTransaction);
            dto.setTotalAmount(filteredTransaction.get(0).getBalance());
            externalBankTransactionDto.add(dto);

            returnObject.put("AssetExternalBankTransaction", externalBankTransactionDto);
        }


        AccountHead depositAccountHead = accountHeadService.findByName("Deposit");
        if (depositAccountHead != null) {
            List<BalanceSheetDto> balanceSheetDtos = null;
            List<Ledger> ledgers = accountHeadService.getAccountHeadLedgers(depositAccountHead);
            if (ledgers != null && ledgers.size() > 0) {
                balanceSheetDtos = new ArrayList<>();
                for (Ledger ledger : ledgers) {
                    BalanceSheetDto dto = new BalanceSheetDto();
                    List<Transaction> transactions = transactionService.getAllTransactions(inputBalanceSheetDto.getStartDate(),inputBalanceSheetDto.getEndDate(),ledger);
                    if (transactions != null && transactions.size() > 0) {
                        BigDecimal totalLedger = transactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
                        dto.setTotalAmount(totalLedger);
                        dto.setLiabilityTransactions(transactions);
                        dto.setLedger(ledger);
                        balanceSheetDtos.add(dto);
                    }
                }
            }

            returnObject.put("LiabilityDeposit", balanceSheetDtos);
        }


//        AccountHead memberDepositAccountHead = accountHeadService.findByName("Deposit");
//        List<Transaction> memberDepositTransactions = transactionService.getAllTransactions(memberDepositAccountHead);
//        if (memberDepositTransactions != null && memberDepositTransactions.size() > 0) {
//            List<BalanceSheetDto> balanceSheetDtos = new ArrayList<>();
//            BalanceSheetDto dto = new BalanceSheetDto();
//            List<Transaction> filteredTransaction = memberDepositTransactions.stream().filter(item -> item.getCreditAmount().intValue() > 0).collect(Collectors.toList());
//
//            dto.setLiabilityTransactions(filteredTransaction);
//            dto.setAccountHead(memberDepositAccountHead);
//            BigDecimal totalDeposit = memberDepositTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
//            dto.setTotalAmount(totalDeposit);
//            balanceSheetDtos.add(dto);
//            returnObject.put("LiabilityDeposit", balanceSheetDtos);
//        }

        AccountHead memberShareCapitalAccountHead = accountHeadService.findByName("Share Capital");
        if (shareFeesAccountHead != null) {
            List<BalanceSheetDto> balanceSheetDtos = null;
            List<Ledger> ledgers = accountHeadService.getAccountHeadLedgers(shareFeesAccountHead);
            if (ledgers != null && ledgers.size() > 0) {
                balanceSheetDtos = new ArrayList<>();
                for (Ledger ledger : ledgers) {
                    if (ledger.getName().equalsIgnoreCase("Share Capital")) {
                        BalanceSheetDto dto = new BalanceSheetDto();
                        List<Transaction> transactions = transactionService.getAllTransactions(inputBalanceSheetDto.getStartDate(),inputBalanceSheetDto.getEndDate(),ledger);
                        if (transactions != null && transactions.size() > 0) {
                            BigDecimal totalLedger = transactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
                            dto.setTotalAmount(totalLedger);
                            dto.setLiabilityTransactions(transactions);
                            dto.setLedger(ledger);
                            balanceSheetDtos.add(dto);
                        }
                    }
                }

            }
            returnObject.put("LiabilityShareCapital", balanceSheetDtos);
        }
//        AccountHead memberShareCapitalAccountHead = accountHeadService.findByName("Share Capital");
//        List<Transaction> memberShareCapitalTransactions = transactionService.getAllTransactions(memberShareCapitalAccountHead);
//        if (memberShareCapitalTransactions != null && memberShareCapitalTransactions.size() > 0) {
//            List<BalanceSheetDto> balanceSheetDtos = new ArrayList<>();
//            BalanceSheetDto dto = new BalanceSheetDto();
//
//            List<Transaction> filteredTransaction = memberShareCapitalTransactions.stream().filter(item -> item.getCreditAmount().intValue() > 0).collect(Collectors.toList());
//
//            dto.setLiabilityTransactions(filteredTransaction);
//            dto.setAccountHead(memberShareCapitalAccountHead);
//            BigDecimal totalShareCapital = memberShareCapitalTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
//            dto.setTotalAmount(totalShareCapital);
//            balanceSheetDtos.add(dto);
//            returnObject.put("LiabilityShareCapital", balanceSheetDtos);
//        }

        return new ResponseEntity<>(returnObject, HttpStatus.OK);
    }

    @GetMapping("/audit/trial-balance-sheet")
    public ResponseEntity<Map<String, List<BalanceSheetDto>>> trialBalanceReport() {

        Map<String, List<BalanceSheetDto>> returnObject = new HashMap<>();

        AccountHead shareFeesAccountHead = accountHeadService.findByName("Share Capital");
        if (shareFeesAccountHead != null) {
            List<BalanceSheetDto> balanceSheetDtos = null;
            List<Ledger> ledgers = accountHeadService.getAccountHeadLedgers(shareFeesAccountHead);
            if (ledgers != null && ledgers.size() > 0) {
                balanceSheetDtos = new ArrayList<>();
                for (Ledger ledger : ledgers) {
                    BalanceSheetDto dto = new BalanceSheetDto();
                    List<Transaction> transactions = transactionService.getAllTransactions(ledger);
                    if (transactions != null && transactions.size() > 0) {
                        BigDecimal totalLedger = transactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
                        dto.setTotalAmount(totalLedger);
                        dto.setAssetTransactions(transactions);
                        dto.setLedger(ledger);
                        balanceSheetDtos.add(dto);
                    }
                }

            }
            returnObject.put("AssetShareFees", balanceSheetDtos);
        }


        AccountHead memberLoanAccountHead = accountHeadService.findByName("Loan");
        List<Transaction> memberLoanTransactions = transactionService.getAllTransactions(shareFeesAccountHead);
        if (memberLoanTransactions != null && memberLoanTransactions.size() > 0) {
            List<BalanceSheetDto> shareFeeProfitDto = new ArrayList<>();

            BalanceSheetDto dto = new BalanceSheetDto();
            dto.setAssetTransactions(memberLoanTransactions);
            dto.setAccountHead(memberLoanAccountHead);

            BigDecimal totalShareFee = memberLoanTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalShareFee);
            shareFeeProfitDto.add(dto);

            returnObject.put("AssetLoan", shareFeeProfitDto);
        }

        Ledger loanInterestChargeLedger = accountHeadService.getLedgerByName("Loan Interest");
        List<Transaction> assetLoanInterestTransactions = transactionService.getAllTransactions(loanInterestChargeLedger);
        if (assetLoanInterestTransactions != null && assetLoanInterestTransactions.size() > 0) {
            List<BalanceSheetDto> shareFeeProfitDto = new ArrayList<>();
            BalanceSheetDto dto = new BalanceSheetDto();
            dto.setAssetTransactions(assetLoanInterestTransactions);
            dto.setLedger(loanInterestChargeLedger);
            BigDecimal totalLedger = assetLoanInterestTransactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalLedger);
            shareFeeProfitDto.add(dto);
            returnObject.put("AssetLoanInterestCharges", shareFeeProfitDto);
        }

        List<BankTransaction> externalBankTransaction = bankTransactionService.getAllBankTransactions();
        if (externalBankTransaction != null && externalBankTransaction.size() > 0) {
            List<BalanceSheetDto> externalBankTransactionDto = new ArrayList<>();
            BalanceSheetDto dto = new BalanceSheetDto();
            List<BankTransaction> filteredTransaction = externalBankTransaction.stream().filter(item -> item.getBalance().intValue() > 0).collect(Collectors.toList());
            dto.setExternalBankTransactions(filteredTransaction);
            BigDecimal totalLedger = externalBankTransaction.stream().map(BankTransaction::getBalance).reduce(BigDecimal::add).get();
            dto.setTotalAmount(totalLedger);
            externalBankTransactionDto.add(dto);
            returnObject.put("AssetExternalBankTransaction", externalBankTransactionDto);
        }


        AccountHead depositAccountHead = accountHeadService.findByName("Deposit");
        if (depositAccountHead != null) {
            List<BalanceSheetDto> balanceSheetDtos = null;
            List<Ledger> ledgers = accountHeadService.getAccountHeadLedgers(depositAccountHead);
            if (ledgers != null && ledgers.size() > 0) {
                balanceSheetDtos = new ArrayList<>();
                for (Ledger ledger : ledgers) {
                    BalanceSheetDto dto = new BalanceSheetDto();
                    List<Transaction> transactions = transactionService.getAllTransactions(ledger);
                    if (transactions != null && transactions.size() > 0) {
                        BigDecimal totalLedger = transactions.stream().map(Transaction::getCreditAmount).reduce(BigDecimal::add).get();
                        dto.setTotalAmount(totalLedger);
                        dto.setLiabilityTransactions(transactions);
                        dto.setLedger(ledger);
                        balanceSheetDtos.add(dto);
                    }
                }

            }
            returnObject.put("LiabilityDeposit", balanceSheetDtos);
        }

        AccountHead loanAccountHead = accountHeadService.findByName("Loan");
        if (shareFeesAccountHead != null) {
            List<BalanceSheetDto> balanceSheetLoanDtos = null;
            List<Ledger> ledgers = accountHeadService.getAccountHeadLedgers(loanAccountHead);
            if (ledgers != null && ledgers.size() > 0) {
                balanceSheetLoanDtos = new ArrayList<>();
                for (Ledger ledger : ledgers) {
                    BalanceSheetDto dto = new BalanceSheetDto();
                    List<Transaction> transactions = transactionService.getAllTransactions(ledger);
                    if (transactions != null && transactions.size() > 0) {
                        BigDecimal totalLedger = transactions.stream().map(Transaction::getDebitAmount).reduce(BigDecimal::add).get();
                        dto.setTotalAmount(totalLedger);
                        dto.setLiabilityTransactions(transactions);
                        dto.setLedger(ledger);
                        balanceSheetLoanDtos.add(dto);
                    }
                }

            }

            AccountHead cropLoanAccountHead = accountHeadService.findByName("Crop Loan");
            if (cropLoanAccountHead != null) {
                List<Ledger> cropLoanLedgers = accountHeadService.getAccountHeadLedgers(cropLoanAccountHead);
                if (cropLoanLedgers != null && cropLoanLedgers.size() > 0) {
                    for (Ledger ledger : cropLoanLedgers) {
                        BalanceSheetDto dto = new BalanceSheetDto();
                        List<Transaction> transactions = transactionService.getAllTransactions(ledger);
                        if (transactions != null && transactions.size() > 0) {
                            BigDecimal totalLedger = transactions.stream().map(Transaction::getDebitAmount).reduce(BigDecimal::add).get();
                            dto.setTotalAmount(totalLedger);
                            dto.setLiabilityTransactions(transactions);
                            dto.setLedger(ledger);
                            balanceSheetLoanDtos.add(dto);
                        }
                    }
                }
            }


            returnObject.put("LiabilityLoan", balanceSheetLoanDtos);
        }

        return new ResponseEntity<>(returnObject, HttpStatus.OK);
    }

    @GetMapping("/audit/balance-sheet/excel")
    public ResponseEntity<List<Transaction>> balanceSheetExcelReport(HttpServletResponse response) throws ServletException, IOException {

        totalLiability = new BigDecimal(0);
        totalAsset = new BigDecimal(0);

        OutputStream out = null;
        try {

            String fileName = "Balance Sheet Report";
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");

            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet workSheet = w.createSheet("Balance Sheet", 0);


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


            workSheet.addCell(new Label(0, 2, "Liability", headerFormat));
            workSheet.addCell(new Label(1, 2, "Amount", headerFormat));
            workSheet.addCell(new Label(2, 2, "Amount", headerFormat));

            workSheet.addCell(new Label(4, 2, "Asset", headerFormat));
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
                            workSheet.addCell(new Label(2, rowCount, "" + calculateLiablityTotal(creditDebitReport.get(i).getAccountCode()), totalFormat));

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
                            workSheet.addCell(new Label(6, rowCount, "" + calculateAssetTotal(creditDebitReport.get(i).getAccountCode()), totalFormat));

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
                workSheet.addCell(new Label(3, count, "" + totalLiability, totalFormat));

                workSheet.addCell(new Label(4, count, "Total", totalFormat));
                workSheet.addCell(new Label(5, count, "", totalFormat));
                workSheet.addCell(new Label(6, count, "", totalFormat));
                workSheet.addCell(new Label(7, count, "" + totalAsset, totalFormat));

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

    private BigDecimal calculateLiablityTotal(long code) {
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
        totalLiability = totalLiability.add(total);
        return total;
    }

    private BigDecimal calculateAssetTotal(long code) {
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
        totalAsset = totalAsset.add(total);
        return total;
    }
}