package com.dfq.coeffi.cbs.expense.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;
import com.dfq.coeffi.cbs.deposit.service.PigmyAgentService;
import com.dfq.coeffi.cbs.expense.dto.ExpenseDto;
import com.dfq.coeffi.cbs.expense.entity.Expense;
import com.dfq.coeffi.cbs.expense.service.ExpenseService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class ExpenseApi extends BaseController {

    private final ExpenseService expenseService;
    private final ApplicationLogService applicationLogService;
    private final BankService bankService;
    private final BODDateService bodDateService;
    private final AccountHeadService accountHeadService;
    private final TransactionService transactionService;
    private final PigmyAgentService pigmyAgentService;

    @Autowired
    private ExpenseApi(final ExpenseService expenseService, final ApplicationLogService applicationLogService,
                       final BankService bankService, final BODDateService bodDateService,
                       final AccountHeadService accountHeadService, final TransactionService transactionService,
                       final PigmyAgentService pigmyAgentService) {
        this.expenseService = expenseService;
        this.applicationLogService = applicationLogService;
        this.bankService = bankService;
        this.bodDateService = bodDateService;
        this.accountHeadService = accountHeadService;
        this.transactionService = transactionService;
        this.pigmyAgentService = pigmyAgentService;
    }

    @GetMapping("/expense")
    public ResponseEntity<List<Expense>> getExpenses() {
        List<Expense> expenses = expenseService.getExpenses();
        if (CollectionUtils.isEmpty(expenses)) {
            log.warn("No expenses found");
            throw new EntityNotFoundException("expenses");
        }
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @PostMapping("/expense")
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense, Principal principal) {
        Expense persistedObject = null;
        User loggedUser = getLoggedUser(principal);
        bodDateService.checkBOD();

        Optional<BankMaster> bankMasterObj = bankService.getActiveBank();
        if (!bankMasterObj.isPresent()) {
            log.warn("No active bank found");
            throw new EntityNotFoundException(BankMaster.class.getName());
        }
        BankMaster bankMaster = bankMasterObj.get();


        if (expense.getRemunerationType() != null && expense.getRemunerationType().equalsIgnoreCase("agent")) {
            Optional<PigmyAgent> pigmyAgentOptional = pigmyAgentService.getPigmyAgentById(expense.getPigmyAgentId());
            PigmyAgent pigmyAgent = pigmyAgentOptional.get();
            expense.setCollectionAgent(pigmyAgent);

            persistedObject = expenseService.saveExpense(expense);
            if (persistedObject != null) {
                pigmyDebitTransactionEntry(persistedObject, bankMaster, loggedUser);
                pigmyTaxCreditTransactionEntry(persistedObject, bankMaster, loggedUser);
            }
        } else {
            persistedObject = expenseService.saveExpense(expense);
            if (persistedObject != null) {
                expenseDebitTransactionEntry(persistedObject, bankMaster, loggedUser);
            }
        }

        applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Expense Id" + persistedObject.getId() + " submitted",
                "EXPENSE CREATED", loggedUser.getId());


        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    // EXPENSE DEBIT TRANSACTION
    private void expenseDebitTransactionEntry(Expense expense, BankMaster bankMaster, User user) {

        BigDecimal balanceAmount = bankMaster.getBalance();
        balanceAmount = balanceAmount.subtract(expense.getAmount());
        bankMaster.setBalance(balanceAmount);

        bankService.saveBankMaster(bankMaster);

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For debit transaction against the refund share
            Transaction debitTransaction = new Transaction();

            debitTransaction.setDebitAmount(expense.getAmount());
            debitTransaction.setRemark("Amount debited from main balance for : " + expense.getDescription());
            debitTransaction.setTransactionBy(user);
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setAccountNumber("");
            debitTransaction.setTransferType("Cash");
            debitTransaction.setAccountHead(expense.getAccountHead());
            debitTransaction.setLedger(expense.getLedger());

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(expense.getAmount());
            debitTransaction.setBalance(balance);

            transactionService.transactionEntry(debitTransaction);

            if (debitTransaction != null) {
                String message = "" + debitTransaction.getDebitAmount() + " Amount debited from main balance for  : " + expense.getDescription();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Expense Module", user.getId());
            }
            if (debitTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount debited from society bank Acc No. " + debitTransaction.getId() + " submitted",
                        "EXPENSE", user.getId());
            }
        }
    }

    private void pigmyDebitTransactionEntry(Expense expense, BankMaster bankMaster, User user) {

        Ledger ledger = accountHeadService.getLedgerByName("Pigmy Deposit");
        BigDecimal balanceAmount = bankMaster.getBalance();
        balanceAmount = balanceAmount.subtract(expense.getCommissionAmount());
        bankMaster.setBalance(balanceAmount);
        bankService.saveBankMaster(bankMaster);

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(expense.getCommissionAmount());
            debitTransaction.setRemark("Amount debited from main balance for : " + expense.getDescription());
            debitTransaction.setTransactionBy(user);
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setAccountNumber("");
            debitTransaction.setTransferType("Cash");
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(expense.getCommissionAmount());
            debitTransaction.setBalance(balance);

            transactionService.transactionEntry(debitTransaction);

            if (debitTransaction != null) {
                String message = "" + debitTransaction.getDebitAmount() + " Amount debited from main balance for  : " + expense.getDescription();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Remuneration Module", user.getId());
            }
            if (debitTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount debited from society bank Acc No. " + debitTransaction.getId() + " submitted",
                        "REMUNERATION", user.getId());
            }
        }
    }

    private void pigmyTaxCreditTransactionEntry(Expense expense, BankMaster bankMaster, User user) {

        Ledger ledger = accountHeadService.getLedgerByName("TDS");
        BigDecimal balanceAmount = bankMaster.getBalance();
        balanceAmount = balanceAmount.add(expense.getTds());
        bankMaster.setBalance(balanceAmount);
        bankService.saveBankMaster(bankMaster);

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(new BigDecimal(0));
            debitTransaction.setRemark("Amount debited from main balance for : " + expense.getDescription());
            debitTransaction.setTransactionBy(user);
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("CREDIT");
            debitTransaction.setCreditAmount(expense.getTds());
            debitTransaction.setAccountNumber("");
            debitTransaction.setTransferType("Cash");
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(expense.getTds());
            debitTransaction.setBalance(balance);

            transactionService.transactionEntry(debitTransaction);

            if (debitTransaction != null) {
                String message = "" + debitTransaction.getCreditAmount() + " Tds amount credited";
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Remuneration Module", user.getId());
            }
            if (debitTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount Credited to society bank Acc No. " + debitTransaction.getId() + " submitted",
                        "REMUNERATION", user.getId());
            }
        }
    }

    @GetMapping("/expense/{id}")
    public ResponseEntity<Expense> getExpense(@PathVariable Long id) {
        Optional<Expense> expense = expenseService.getExpense(id);
        if (!expense.isPresent()) {
            log.warn("Unable to find expense with ID : {} not found", id);
            throw new EntityNotFoundException(Expense.class.getName());
        }
        return new ResponseEntity<>(expense.get(), HttpStatus.OK);
    }

    @PostMapping("/expense-consolidation")
    public ResponseEntity<List<ExpenseDto>> getExpenseConsolidations(@RequestBody ExpenseDto expenseDto) {
        List<ExpenseDto> expenseDtos = new ArrayList<>();
        AccountHead accountHead = accountHeadService.findByName("Expense");
        List<Ledger> ledgers = accountHeadService.getAccountHeadLedgers(accountHead);
        if (ledgers != null && ledgers.size() > 0) {
            for (Ledger ledger : ledgers) {
                ExpenseDto dto = new ExpenseDto();
                List<Expense> expenses = expenseService.getExpenses(ledger.getId(), expenseDto.getFromDate(), expenseDto.getToDate());
                if (expenses != null && expenses.size() > 0) {
                    BigDecimal ledgerTotal = expenses
                            .stream()
                            .map(Expense::getAmount)
                            .reduce(BigDecimal::add)
                            .get();
                    dto.setAmount(ledgerTotal);
                    dto.setLedger(ledger);
                } else {
                    dto.setAmount(new BigDecimal(0));
                    dto.setLedger(ledger);
                }
                expenseDtos.add(dto);
            }
        }

        return new ResponseEntity<>(expenseDtos, HttpStatus.OK);
    }
}