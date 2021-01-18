package com.dfq.coeffi.cbs.report.accountReports;

import com.dfq.coeffi.cbs.expense.entity.Expense;
import com.dfq.coeffi.cbs.expense.service.ExpenseService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.repository.LedgerRepository;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.report.PDFExcelFunction;
import com.dfq.coeffi.cbs.report.depositReports.DepositReportDto;
import com.dfq.coeffi.cbs.transaction.entity.BankTransaction;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.BankTransactionService;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Slf4j
@RestController
@Transactional
public class AccountReportApi extends BaseController {

    @Autowired
    private AccountReportService accountReportService;

    @Autowired
    private PDFExcelFunction pdfExcelFunction;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private AccountHeadService accountHeadService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private BankTransactionService bankTransactionService;

    @PostMapping("report/accounts/cash-book")
    public ResponseEntity<InputStreamResource> getCashBookDetails(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {

        String newYearDate = "01/04/2019";
        String startDate = "01/04/2019";
        String endDate = "01/04/2019";
        Date newYearD = DateUtil.convertToDate(newYearDate);
        Date sDate = DateUtil.convertToDate(startDate);
        Date end = DateUtil.convertToDate(endDate);
        if(depositReportDto.dateFrom.before(newYearD) || depositReportDto.dateTo.before(newYearD)){
            depositReportDto.setDateFrom(sDate);
            depositReportDto.setDateTo(end);
        }
        List<Transaction> transactions = null;

        if (depositReportDto.reportType.equalsIgnoreCase("list")) {

            if (depositReportDto.getLedger().equalsIgnoreCase("all")) {
                transactions = accountReportService.getCashBookDetails(depositReportDto.dateFrom, depositReportDto.dateTo);

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

            } else {
                transactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, depositReportDto.getLedger());
            }
            if (CollectionUtils.isEmpty(transactions)) {
                log.warn("No CashBook transactions found");
                throw new EntityNotFoundException("No CashBook transactions found");
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getCashBookDetailsPDF(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getCashBookDetailsExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(transactions, HttpStatus.OK);
    }

    @PostMapping("report/accounts/day-book")
    public ResponseEntity<InputStreamResource> getDayBookDetails(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {
        List<Transaction> transactions = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            transactions = accountReportService.getDayBookDetails(depositReportDto.dateFrom, depositReportDto.dateTo);
            if (CollectionUtils.isEmpty(transactions)) {
                log.warn("No Daybook transactions found");
                throw new EntityNotFoundException("No Daybook transactions found");
            }

            List<Transaction> shareCapitalTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Share Capital");
            List<Transaction> shareFeeTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Share Fee");
            List<Transaction> otherFeeTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Other Fee");
            List<Transaction> entryTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Entry Fee");

        } else if (depositReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getDayBookDetailsPDF(depositReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (depositReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getDayBookDetailsExcel(depositReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(transactions, HttpStatus.OK);
    }

    @PostMapping("report/accounts/trial-balance")
    public ResponseEntity<InputStreamResource> getTrialBalanceDetails(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<Transaction> transactions = null;
        Map<String, BigDecimal> pigmyDepositMap = null;
        Map<String, BigDecimal> savingBankMap = null;
        Map<String, BigDecimal> currentAccountMap, childrensDepositMap = null;
        ArrayList arrayList = null;
        JSONObject json = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            transactions = accountReportService.getTrialBalance(depositReportDto.dateFrom, depositReportDto.dateTo);
            if (CollectionUtils.isEmpty(transactions)) {
                log.warn("No TrialBalance Details  found");
                throw new EntityNotFoundException("No TrialBalance Details found");
            }
            BigDecimal pigmyDepositClosingBalance, pigmyDepositCredit, pigmyDepositDebit, pigmyDepositOpeningBalance;
            pigmyDepositCredit = pigmyDepositDebit = pigmyDepositOpeningBalance = pigmyDepositClosingBalance = BigDecimal.ZERO;
            BigDecimal savingBankClosingBalance, savingBankCredit, savingBankDebit, savingBankOpeningBalance;
            savingBankClosingBalance = savingBankCredit = savingBankDebit = savingBankOpeningBalance = BigDecimal.ZERO;
            BigDecimal currentAccountClosingBalance, currentAccountCredit, currentAccountDebit, currentAccountOpeningBalance;
            currentAccountClosingBalance = currentAccountCredit = currentAccountDebit = currentAccountOpeningBalance = BigDecimal.ZERO;
            BigDecimal childrensDepositClosingBalance, childrensDepositCredit, childrensDepositDebit, childrensDepositOpeningBalance;
            childrensDepositClosingBalance = childrensDepositCredit = childrensDepositDebit = childrensDepositOpeningBalance = BigDecimal.ZERO;
            for (int i = 0; i < transactions.size(); i++) {
                if (transactions.get(i).getParticulars().equalsIgnoreCase("PIGMY DEPOSIT")) {
                    pigmyDepositClosingBalance = transactions.get(i).getBalance().add(pigmyDepositClosingBalance);
                    pigmyDepositCredit = transactions.get(i).getCreditAmount().add(pigmyDepositCredit);
                    pigmyDepositDebit = transactions.get(i).getDebitAmount().add(pigmyDepositDebit);
                    pigmyDepositOpeningBalance = pigmyDepositClosingBalance.subtract(pigmyDepositCredit.subtract(pigmyDepositDebit));
                } else if (transactions.get(i).getParticulars().equalsIgnoreCase("SAVING BANK DEPOSIT")) {
                    savingBankClosingBalance = transactions.get(i).getBalance().add(savingBankClosingBalance);
                    savingBankCredit = transactions.get(i).getCreditAmount().add(savingBankCredit);
                    savingBankDebit = transactions.get(i).getDebitAmount().add(savingBankDebit);
                    savingBankOpeningBalance = savingBankClosingBalance.subtract(savingBankCredit.subtract(savingBankDebit));
                } else if (transactions.get(i).getParticulars().equalsIgnoreCase("CURRENT ACCOUNT DEPOSIT")) {
                    currentAccountClosingBalance = transactions.get(i).getBalance().add(currentAccountClosingBalance);
                    currentAccountCredit = transactions.get(i).getCreditAmount().add(currentAccountCredit);
                    currentAccountDebit = transactions.get(i).getDebitAmount().add(currentAccountDebit);
                    currentAccountOpeningBalance = currentAccountClosingBalance.subtract(currentAccountCredit.subtract(currentAccountDebit));
                } else if (transactions.get(i).getParticulars().equalsIgnoreCase("CHILDRENS DEPOSIT")) {
                    childrensDepositClosingBalance = transactions.get(i).getBalance().add(childrensDepositClosingBalance);
                    childrensDepositCredit = transactions.get(i).getCreditAmount().add(childrensDepositCredit);
                    childrensDepositDebit = transactions.get(i).getDebitAmount().add(childrensDepositDebit);
                    childrensDepositOpeningBalance = childrensDepositClosingBalance.subtract(childrensDepositCredit.subtract(childrensDepositDebit));
                }
            }
            arrayList = new ArrayList();
            pigmyDepositMap = new HashMap<>();
            pigmyDepositMap.put("pigmyDepositOpeningBalance", pigmyDepositOpeningBalance);
            pigmyDepositMap.put("pigmyDepositCredit", pigmyDepositCredit);
            pigmyDepositMap.put("pigmyDepositDebit", pigmyDepositDebit);
            pigmyDepositMap.put("pigmyDepositClosingBalance", pigmyDepositClosingBalance);

            savingBankMap = new HashMap<>();
            savingBankMap.put("savingBankOpeningBalance", savingBankOpeningBalance);
            savingBankMap.put("savingBankCredit", savingBankCredit);
            savingBankMap.put("savingBankDebit", savingBankDebit);
            savingBankMap.put("savingBankClosingBalance", savingBankClosingBalance);

            currentAccountMap = new HashMap<>();
            currentAccountMap.put("currentAccountOpeningBalance", currentAccountOpeningBalance);
            currentAccountMap.put("currentAccountCredit", currentAccountCredit);
            currentAccountMap.put("currentAccountDebit", currentAccountDebit);
            currentAccountMap.put("currentAccountClosingBalance", currentAccountClosingBalance);

            childrensDepositMap = new HashMap<>();
            childrensDepositMap.put("childrensDepositOpeningBalance", childrensDepositOpeningBalance);
            childrensDepositMap.put("childrensDepositCredit", childrensDepositCredit);
            childrensDepositMap.put("childrensDepositDebit", childrensDepositDebit);
            childrensDepositMap.put("childrensDepositClosingBalance", childrensDepositClosingBalance);

            arrayList.add(pigmyDepositMap);
            arrayList.add(savingBankMap);
            arrayList.add(currentAccountMap);
            arrayList.add(childrensDepositMap);
        }
        return new ResponseEntity(arrayList, HttpStatus.OK);
    }

    @PostMapping("report/accounts/day-book/transfer-type")
    public ResponseEntity<InputStreamResource> getDayBookDetailsByTransferType(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {
        List<Transaction> transactions = null;
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            transactions = accountReportService.getDayBookDetailsByTransferType(depositReportDto.dateFrom, depositReportDto.dateTo, "Transfer");
            if (CollectionUtils.isEmpty(transactions)) {
                log.warn("No Daybook transactions found");
                throw new EntityNotFoundException("No Daybook transactions found");
            }
        }
        return new ResponseEntity(transactions, HttpStatus.OK);
    }

    @PostMapping("report/accounts/general-ledger")
    public ResponseEntity<Map<String, List<Transaction>>>  getGeneralLedgerDetails(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {

        /*BigDecimal expenseAmount = getExpenses(depositReportDto.dateFrom, depositReportDto.dateTo);

        List<Transaction> transactions = null;
        List<BankTransaction> bankTransactions = null;

        List<GeneralLedgerDto> generalLedgerDtos = new ArrayList<>();
        List<AccountHead> accountHeads = accountHeadService.getAccountHeads();
        List<Ledger> ledgers = ledgerRepository.findByActive(true);
        for (AccountHead accountHead : accountHeads) {
            GeneralLedgerDto generalLedgerDto = new GeneralLedgerDto();
            List<LedgerReportDto> ledgerReportDtos = new ArrayList<>();

            for (Ledger ledger : ledgers) {
                if (ledger.getAccountHead().getId() == accountHead.getId()) {
                    LedgerReportDto ledgerReportDto = new LedgerReportDto();

                    BigDecimal totalCreditAmount = BigDecimal.ZERO;
                    BigDecimal totalDebitAmount = BigDecimal.ZERO;

                    BigDecimal totalCreditAmountForBank = BigDecimal.ZERO;
                    BigDecimal totalDebitAmountForBank = BigDecimal.ZERO;

                    // TRANSACTIONS
                    transactions = transactionService.getAllTransactions(depositReportDto.dateFrom, depositReportDto.dateTo);

                    for (Transaction transaction : transactions) {
                        if (ledger.getId() == transaction.getLedger().getId()) {
                            if (transaction.getTransactionType().equalsIgnoreCase("CREDIT")) {
                                if(transaction.getAccountHead().getName().equalsIgnoreCase("Share Capital")){

                                    BigDecimal shareCapital =  transaction.getCreditAmount();
                                    shareCapital = shareCapital.subtract(transaction.getOtherFees());
                                    totalCreditAmount = totalCreditAmount.add(shareCapital);


                                }else{
                                    totalCreditAmount = totalCreditAmount.add(transaction.getCreditAmount());

                                }
                            } else if (transaction.getTransactionType().equalsIgnoreCase("DEBIT")) {
                                totalDebitAmount = totalDebitAmount.add(transaction.getDebitAmount());
                            }
                        }
                    }

                    //BANK TRANSACTIONS
                    bankTransactions = bankTransactionService.getBankTransactionsByBetweenDates(depositReportDto.dateFrom, depositReportDto.dateTo);

                    for (BankTransaction bankTransaction : bankTransactions) {
                        if (ledger.getId() == bankTransaction.getLedger().getId()) {
                            if (bankTransaction.getTransactionType().equalsIgnoreCase("CREDIT")) {
                                totalCreditAmountForBank = totalCreditAmount.add(bankTransaction.getCreditAmount());
                            } else if (bankTransaction.getTransactionType().equalsIgnoreCase("DEBIT")) {
                                totalDebitAmountForBank = totalDebitAmount.add(bankTransaction.getDebitAmount());
                            }
                        }
                    }

                    if (ledger.getName().equalsIgnoreCase("External Bank Accounts")){
                        ledgerReportDto.setCreditAmount(totalCreditAmountForBank);
                        ledgerReportDto.setDebitAmount(totalDebitAmountForBank);

                        ledgerReportDto.setBalanceAmount(totalCreditAmountForBank.subtract(totalDebitAmountForBank));

                        ledgerReportDto.setLedgerName(ledger.getName());
                        ledgerReportDto.setAccountHeadName(ledger.getAccountHead().getName());
                    } else {
                        ledgerReportDto.setCreditAmount(totalCreditAmount);
                        ledgerReportDto.setDebitAmount(totalDebitAmount);

                        ledgerReportDto.setBalanceAmount(totalCreditAmount.subtract(totalDebitAmount));

                        ledgerReportDto.setLedgerName(ledger.getName());
                        ledgerReportDto.setAccountHeadName(ledger.getAccountHead().getName());
                    }

                    if (!ledger.getAccountHead().getName().equalsIgnoreCase("Expense"))

                    ledgerReportDtos.add(ledgerReportDto);
                }
            }

            generalLedgerDto.setAccountHead(accountHead.getName());
            generalLedgerDto.setLedgerReportDtos(ledgerReportDtos);
            BigDecimal creditAmount = BigDecimal.ZERO;
            BigDecimal debitAmount = BigDecimal.ZERO;
            BigDecimal balanceAmount;

            for (LedgerReportDto ledger : ledgerReportDtos) {
                creditAmount = creditAmount.add(ledger.getCreditAmount());
                debitAmount = debitAmount.add(ledger.getDebitAmount());
            }

            if (generalLedgerDto.getAccountHead().equalsIgnoreCase("Expense")) {
                generalLedgerDto.setCreditAmount(BigDecimal.ZERO);
                generalLedgerDto.setDebitAmount(expenseAmount);
            } else {
                generalLedgerDto.setCreditAmount(creditAmount);
                generalLedgerDto.setDebitAmount(debitAmount);
            }

            if (generalLedgerDto.getAccountHead().equalsIgnoreCase("Expense")) {
                balanceAmount = expenseAmount.subtract(BigDecimal.ZERO);
            } else {
                balanceAmount = creditAmount.subtract(debitAmount);
            }

            generalLedgerDto.setBalanceAmount(balanceAmount);

            if (!generalLedgerDto.getAccountHead().equalsIgnoreCase("Bank Accounts") && !generalLedgerDto.getAccountHead().equalsIgnoreCase("Cash Account"))
                generalLedgerDtos.add(generalLedgerDto);
        }*/
        Map<String, List<Transaction>> map = new HashMap<>();

        List<Transaction> shareCapitalTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Share Capital");
        List<Transaction> shareFeeTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Share Fees");
        List<Transaction> otherFeeTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Other Fees");
        List<Transaction> entryTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Entry Fees");


        List<Transaction> savingBankDepositTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Saving Bank Deposit");
        List<Transaction> currentAccountTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Current Account");
        List<Transaction> fixedDepositTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Fixed Deposit");
        List<Transaction> recurringDepositTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Recurring Deposit");
        List<Transaction> termDepositTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Term Deposit");
        List<Transaction> pigmyDepositTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Pigmy Deposit");
        List<Transaction> longTermCertificateDepositTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Long Term Certificate Deposit");

        List<Transaction> goldLoanTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Gold");
        List<Transaction> termLoanTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Term");
        List<Transaction> ladLoanTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Lad");
        List<Transaction> cropLoanTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Crop");
        List<Transaction> mkcclLoanTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Mkccl");
        List<Transaction> mtLoanTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Mt");
        List<Transaction> loanServiceCharges = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Loan Service Charges");
        List<Transaction> loanInterest = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Loan Interest");

        List<Transaction> cashAccounts = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Cash Account");
        List<Transaction> bankAccounts = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "Bank Accounts");

//      List<Transaction> expenseTransactions = transactionReportService.getTransactionByAccountHeadAndBetweenDate(depositReportDto.dateFrom, depositReportDto.dateTo, "Expense");

        List<Transaction> externalBankAccountsTransactions = accountReportService.getCashBookDetailsByLedger(depositReportDto.dateFrom, depositReportDto.dateTo, "External Bank Accounts");

        List<BankTransaction> bankTransactions = bankTransactionService.getBankTransactionsByBetweenDates(depositReportDto.dateFrom, depositReportDto.dateTo);
        List<Transaction> transactionList = new ArrayList<>();

        if (bankTransactions != null) {
            for (BankTransaction bankTransaction : bankTransactions) {

                Transaction transaction = new Transaction();

                transaction.setCreditAmount(bankTransaction.getCreditAmount());
                transaction.setDebitAmount(bankTransaction.getDebitAmount());
                transaction.setAccountNumber(bankTransaction.getAccountNumber());
                transaction.setAccountHead(bankTransaction.getAccountHead());
                transaction.setLedger(bankTransaction.getLedger());
                transaction.setRemark(bankTransaction.getDescription());
                transaction.setTransferType(bankTransaction.getTransferType());
                transaction.setTransactionType(bankTransaction.getTransactionType());
                transaction.setAccountName(bankTransaction.getBankMaster().getBankName());
                transaction.setBalance(bankTransaction.getBalance());
                transaction.setTransactionOn(bankTransaction.getTransactionOn());

                transactionList.add(transaction);
            }
        }
        externalBankAccountsTransactions.addAll(transactionList);

        map.put("ShareCapital", shareCapitalTransactions);
        map.put("ShareFee", shareFeeTransactions);
        map.put("OtherFee", otherFeeTransactions);
        map.put("EntryFee", entryTransactions);
        map.put("SavingBankDeposit", savingBankDepositTransactions);
        map.put("CurrentAccountDeposit", currentAccountTransactions);
        map.put("FixedDeposit", fixedDepositTransactions);
        map.put("RecurringDeposit", recurringDepositTransactions);
        map.put("TermDeposit", termDepositTransactions);
        map.put("PigmyDeposit", pigmyDepositTransactions);
        map.put("LongTermDeposit", longTermCertificateDepositTransactions);
        map.put("GoldLoan", goldLoanTransactions);
        map.put("TermLoan", termLoanTransactions);
        map.put("LadLoan", ladLoanTransactions);
        map.put("CropLoan", cropLoanTransactions);
        map.put("MKCCLLoan", mkcclLoanTransactions);
        map.put("MTLoan", mtLoanTransactions);
        map.put("LoanServiceCharges", loanServiceCharges);
        map.put("LoanInterest", loanInterest);
        map.put("CashAccount", cashAccounts);
        map.put("BankAccount", bankAccounts);
//        map.put("Expense", expenseTransactions);
        map.put("ExternalBankAccounts", externalBankAccountsTransactions);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    private BigDecimal getCashInHandAmount(Date dateFrom, Date dateTo) {

        BigDecimal creditAmount = BigDecimal.ZERO;
        BigDecimal debitAmount = BigDecimal.ZERO;

        List<Transaction> transactions = transactionService.getTransactionByDateAndTransferType(dateFrom, dateTo, "Cash");

        List<Transaction> transactionCashObj = new ArrayList<>();

        for (Transaction removeTransaction : transactions) {
            if (removeTransaction.getAccountHead().getName().equalsIgnoreCase("Loan")) {
                transactionCashObj.add(removeTransaction);
            }
        }
        transactions.removeAll(transactionCashObj);

        for (Transaction transaction : transactions) {
            creditAmount = creditAmount.add(transaction.getCreditAmount());
            debitAmount = debitAmount.add(transaction.getDebitAmount());
        }

        BigDecimal cashInHandBalanceAmount = creditAmount.subtract(debitAmount);

        return cashInHandBalanceAmount;
    }

    private BigDecimal getExpenses(Date dateFrom, Date dateTo) {
        List<Expense> expenses = expenseService.getExpenseBetweenDates(dateFrom, dateTo);

        BigDecimal debitAmount = BigDecimal.ZERO;
        for (Expense expense : expenses) {
            debitAmount = debitAmount.add(expense.getAmount());
        }
        return debitAmount;
    }

}