package com.dfq.coeffi.cbs.transaction.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransactionService;
import com.dfq.coeffi.cbs.deposit.depositTransaction.recurringDepositTransaction.RecurringDepositTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.recurringDepositTransaction.RecurringDepositTransactionService;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.deposit.service.*;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanTransaction;
import com.dfq.coeffi.cbs.loan.service.LoanService;
import com.dfq.coeffi.cbs.loan.service.LoanTransactionService;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.transaction.entity.FundTransfer;
import com.dfq.coeffi.cbs.transaction.entity.FundTransferDto;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.FundTransferService;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.dfq.coeffi.cbs.utils.TransactionUtil;
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
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class FundTransferApi extends BaseController {

    @Autowired
    private FundTransferService fundTransferService;
    @Autowired
    private SavingBankTransactionService savingBankTransactionService;
    @Autowired
    private ApplicationLogService applicationLogService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountHeadService accountHeadService;
    @Autowired
    private BODDateService bodDateService;
    @Autowired
    private SavingsBankDepositService savingsBankDepositService;
    @Autowired
    private CurrentAccountTransactionService currentAccountTransactionService;
    @Autowired
    private CurrentAccountService currentAccountService;
    @Autowired
    private ChildrensDepositService childrensDepositService;
    @Autowired
    private DoubleSchemeService doubleSchemeService;
    @Autowired
    private FixedDepositService fixedDepositService;
    @Autowired
    private TermDepositService termDepositService;
    @Autowired
    public LoanService loanService;
    @Autowired
    public BankService bankService;
    @Autowired
    public MemberService memberService;
    @Autowired
    private LoanTransactionService loanTransactionService;
    @Autowired
    private PigmyDepositService pigmyDepositService;
    @Autowired
    private RecurringDepositService recurringDepositService;
    @Autowired
    private RecurringDepositTransactionService recurringDepositTransactionService;

    @GetMapping("/fund-transfer")
    public ResponseEntity<List<FundTransfer>> getFundTransfers() {
        List<FundTransfer> allFundTransfers = fundTransferService.getAllFundTransfer();
        if (CollectionUtils.isEmpty(allFundTransfers)) {
            throw new EntityNotFoundException("Fund Transfers are not found");
        }
        return new ResponseEntity<>(allFundTransfers, HttpStatus.OK);
    }

    @PostMapping("fund-transfer")
    public ResponseEntity<FundTransfer> createFundTransferTransaction(@Valid @RequestBody final FundTransfer fundTransfer, FundTransferDto fundTransferDto, Principal principal) {
        bodDateService.checkBOD();
        SavingBankTransaction sbDebitTransaction = null;
        if (fundTransfer.getFromAccountType().equalsIgnoreCase("CURRENT_ACCOUNT")) {
            CurrentAccountTransaction caDebitTransaction = currentAccountDebitEntry(fundTransfer, principal);
            if (caDebitTransaction != null) {
                transactionDebitEntry(fundTransfer, principal);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(caDebitTransaction.getDebitAmount(), "DEBIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Current Account Fund Debited", "FundTransfer-DEBIT Operation", loggedUser.getId());
            }
        } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("SAVING_BANK")) {
            RecurringDepositTransaction rdCreditTransaction = null;
            if (fundTransfer.getToAccountNumber().equalsIgnoreCase("RECURRING_DEPOSIT")) {
                rdCreditTransaction = recurringDepositTransactionCreditEntry(fundTransfer, principal);
                if (rdCreditTransaction != null) {
                    transactionCreditEntry(fundTransfer, principal);
                    TransactionUtil transactionUtil = new TransactionUtil(bankService);
                    transactionUtil.getUpdateSocietyBalance(rdCreditTransaction.getCreditAmount(), "CREDIT");
                    User loggedUser = getLoggedUser(principal);
                    applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Recurring Deposit Account Fund Credited", "FundTransfer-CREDIT Operation", loggedUser.getId());
                }
                if (rdCreditTransaction != null) {
                    SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankDepositByAccountNumber(fundTransfer.getFromAccountNumber());
                    sbDebitTransaction = savingBankTransactionDebitEntry(fundTransfer, principal);
                    if (sbDebitTransaction != null) {
                        transactionDebitEntryForSavingBank(fundTransfer, principal, savingsBankDeposit);
                        TransactionUtil transactionUtil = new TransactionUtil(bankService);
                        transactionUtil.getUpdateSocietyBalance(sbDebitTransaction.getDebitAmount(), "DEBIT");
                        User loggedUser = getLoggedUser(principal);
                        applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Deposit Account Fund Debited", "FundTransfer-DEBIT Operation", loggedUser.getId());
                    }
                }
            }

                SavingBankTransaction sbCreditTransaction = null;
                if (fundTransfer.getToAccountType().equalsIgnoreCase("SAVING_BANK")) {
                    SavingsBankDeposit savingsBankDepositFrom = savingsBankDepositService.getSavingsBankDepositByAccountNumber(fundTransfer.getToAccountNumber());

                    sbCreditTransaction = savingBankTransactionCreditEntry(fundTransfer, principal);
                    if (sbCreditTransaction != null) {
                        transactionCreditEntryForSavingBank(fundTransfer, principal, savingsBankDepositFrom);
                        TransactionUtil transactionUtil = new TransactionUtil(bankService);
                        transactionUtil.getUpdateSocietyBalance(sbCreditTransaction.getCreditAmount(), "CREDIT");
                        User loggedUser = getLoggedUser(principal);
                        applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Saving Bank Account Fund Credited", "FundTransfer-CREDIT Operation", loggedUser.getId());
                    }

                if(sbCreditTransaction != null){
                    SavingsBankDeposit savingsBankDepositTo = savingsBankDepositService.getSavingsBankDepositByAccountNumber(fundTransfer.getFromAccountNumber());
                    sbDebitTransaction = savingBankTransactionDebitEntry(fundTransfer, principal);
                    if (sbDebitTransaction != null) {
                        transactionDebitEntryForSavingBank(fundTransfer, principal, savingsBankDepositTo);
                        TransactionUtil transactionUtil = new TransactionUtil(bankService);
                        transactionUtil.getUpdateSocietyBalance(sbDebitTransaction.getDebitAmount(), "DEBIT");
                        User loggedUser = getLoggedUser(principal);
                        applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Deposit Account Fund Debited", "FundTransfer-DEBIT Operation", loggedUser.getId());
                    }
                }

                }
        }else if (fundTransfer.getFromAccountType().equalsIgnoreCase("SAVING_BANK")){
            SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankDepositByAccountNumber(fundTransfer.getFromAccountNumber());
            sbDebitTransaction = savingBankTransactionDebitEntry(fundTransfer, principal);
            if (sbDebitTransaction != null) {
                transactionDebitEntryForSavingBank(fundTransfer, principal, savingsBankDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(sbDebitTransaction.getDebitAmount(), "DEBIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Deposit Account Fund Debited", "FundTransfer-DEBIT Operation", loggedUser.getId());
            }
        }
        else if (fundTransfer.getFromAccountType().equalsIgnoreCase("CHILDRENS_DEPOSIT")) {
            ChildrensDeposit childrensDeposit = childrensDebitEntry(fundTransfer);
            if (childrensDeposit != null) {
                transactionDebitEntry(fundTransfer, principal);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(fundTransfer.getAmount(), "DEBIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Childrens Deposit Account Fund Debited", "FundTransfer-DEBIT Operation", loggedUser.getId());
            }
        } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("DOUBLE_SCHEME")) {
            DoubleScheme doubleScheme = doubleSchemeDebitEntry(fundTransfer);
            if (doubleScheme != null) {
                transactionDebitEntry(fundTransfer, principal);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(fundTransfer.getAmount(), "DEBIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "DoubleScheme Deposit Account Fund Debited", "FundTransfer-DEBIT Operation", loggedUser.getId());
            }
        } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("FIXED_DEPOSIT")) {

            FixedDeposit fixedDeposit = fixedDepositDebitEntry(fundTransfer);
            if (fixedDeposit != null) {
                transactionDebitEntry(fundTransfer, principal);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(fundTransfer.getAmount(), "DEBIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "FixedDeposit Deposit Account Fund Debited", "FundTransfer-DEBIT Operation", loggedUser.getId());
            }
        } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("TERM_DEPOSIT")) {
            TermDeposit termDeposit = termDepositDebitEntry(fundTransfer);
            if (termDeposit != null) {
                transactionDebitEntry(fundTransfer, principal);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(fundTransfer.getAmount(), "DEBIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "TermDeposit Deposit Account Fund Debited", "FundTransfer-DEBIT Operation", loggedUser.getId());
            }

        } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("PIGMY_DEPOSIT")) {

            Optional<PigmyDeposit> pigmyDepositObj = pigmyDepositService.getPigmyDepositByAccountNumber(fundTransfer.getFromAccountNumber());
            if (!pigmyDepositObj.isPresent()) {
                throw new EntityNotFoundException("PigmyDeposit Account not found for Account number :" + fundTransfer.getFromAccountNumber());
            }
            PigmyDeposit pigmyDeposit = pigmyDepositDebitEntry(fundTransfer);

            if (pigmyDeposit != null) {
                transactionDebitEntry(fundTransfer, principal);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(fundTransfer.getAmount(), "DEBIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "TermDeposit Deposit Account Fund Debited", "FundTransfer-DEBIT Operation", loggedUser.getId());
            }
        }else if (fundTransfer.getFromAccountType().equalsIgnoreCase("RECURRING_DEPOSIT")) {

            Optional<RecurringDeposit> recurringDeposit = recurringDepositService.getRecurringDepositByAccountNumber(fundTransfer.getFromAccountNumber());
            if (!recurringDeposit.isPresent()) {
                throw new EntityNotFoundException("Recurring deposit account not found for Account number :" + fundTransfer.getFromAccountNumber());
            }

            recurringDepositService.checkRecurringDepositForLoan(fundTransfer.getFromAccountNumber());
            recurringDepositTransactionEntry(recurringDeposit.get(), fundTransfer);

            RecurringDeposit recurringDepositObj = recurringDeposit.get();
            recurringDepositObj.setWithDrawn(true);

            RecurringDeposit persistedObject = recurringDepositService.saveRecurringDeposit(recurringDepositObj);
            if (persistedObject != null) {
                transactionDebitEntry(fundTransfer, principal);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(fundTransfer.getAmount(), "DEBIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Recurring Deposit Account Fund Debited", "Recurring deposit-DEBIT Operation", loggedUser.getId());
            }
        }


        if (fundTransfer.getToAccountType().equalsIgnoreCase("CURRENT_ACCOUNT")) {
            SavingBankTransaction caCreditTransaction = currentAccountCreditEntry(fundTransfer, principal);
            if (caCreditTransaction != null) {
                transactionCreditEntry(fundTransfer, principal);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(caCreditTransaction.getCreditAmount(), "CREDIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Current Account Fund Credited", "FundTransfer-CREDIT Operation", loggedUser.getId());
            }
        }/* else if (fundTransfer.getToAccountType().equalsIgnoreCase("SAVING_BANK")) {
            SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankDepositByAccountNumber(fundTransfer.getToAccountNumber());

            SavingBankTransaction sbCreditTransaction = savingBankTransactionCreditEntry(fundTransfer, principal);
            if (sbCreditTransaction != null) {
                transactionCreditEntryForSavingBank(fundTransfer, principal, savingsBankDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(sbCreditTransaction.getCreditAmount(), "CREDIT");
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Saving Bank Account Fund Credited", "FundTransfer-CREDIT Operation", loggedUser.getId());
            }
        }*/ /*else if (fundTransfer.getToAccountType().equalsIgnoreCase("RECURRING_DEPOSIT")) {
            if (fundTransfer.getFromAccountType().equalsIgnoreCase("SAVING_BANK")) {
                RecurringDepositTransaction rdCreditTransaction = recurringDepositTransactionCreditEntry(fundTransfer, principal);
                if (rdCreditTransaction != null) {
                    transactionCreditEntry(fundTransfer, principal);
                    TransactionUtil transactionUtil = new TransactionUtil(bankService);
                    transactionUtil.getUpdateSocietyBalance(rdCreditTransaction.getCreditAmount(), "CREDIT");
                    User loggedUser = getLoggedUser(principal);
                    applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Recurring Deposit Account Fund Credited", "FundTransfer-CREDIT Operation", loggedUser.getId());
                }
            } else {
                throw new EntityNotFoundException("Selected from account type is not Saving Bank, so could not trasfer money");
            }

        }*/ else if (fundTransfer.getToAccountType().equalsIgnoreCase("LOAN")) {
            Loan savedLoan = null;
            Optional<Loan> loanObj = loanService.findLoanByAccountNo(fundTransfer.getToAccountNumber());
            if (!loanObj.isPresent()) {
                throw new EntityNotFoundException("Loan not found for account number :" + fundTransfer.getToAccountNumber());
            }
            Loan loan = loanObj.get();

            //In case of gold loan
            if (loan != null && loan.getLoanDetail().getCustomer() != null) {

                Member member = memberService.getMemberByCustomer(loan.getLoanDetail().getCustomer());

                SavingsBankDeposit bankDeposit = savingsBankDepositService.getSavingsBankAccountsByMember(member);
                if (bankDeposit == null) {
                    throw new EntityNotFoundException("Transferring member and loan holder member sb accounts are not matching");
                } else {
                    savedLoan = updateLoanFundTransfer(fundTransfer, loan);
                }
            }

            //In case of term loan
            else if (loan != null && loan.getLoanDetail().getTermDetails() != null && loan.getLoanDetail().getTermDetails().getMember() != null) {
                SavingsBankDeposit bankDeposit = savingsBankDepositService.getSavingsBankAccountsByMember(loan.getLoanDetail().getTermDetails().getMember());
                if (bankDeposit == null) {
                    throw new EntityNotFoundException("Transferring member and loan holder member sb accounts are not matching");
                } else {
                    savedLoan = updateLoanFundTransfer(fundTransfer, loan);
                }
            }

            //In case of FD loan
            else if (loan != null && loan.getLoanDetail().getLadDetails() != null && loan.getLoanDetail().getLadDetails().getFixedDeposit() != null) {
                Member depositHolderMember = loan.getLoanDetail().getLadDetails().getFixedDeposit().getMember();

                SavingsBankDeposit bankDeposit = savingsBankDepositService.getSavingsBankAccountsByMember(depositHolderMember);
                if (bankDeposit == null) {
                    throw new EntityNotFoundException("Transferring member and loan holder member sb accounts are not matching");
                } else {
                    savedLoan = updateLoanFundTransfer(fundTransfer, loan);
                }
            }

            if (savedLoan != null) {
                User loggedUser = getLoggedUser(principal);

                transactionLoanCreditEntry(fundTransfer, savedLoan, principal);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(fundTransfer.getAmount(), "CREDIT");
                fundTransferLoanTransactionEntry(loan, fundTransfer, loggedUser);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Loan Account Amount Credited", "FundTransfer-CREDIT Operation", loggedUser.getId());
            }
        }

        fundTransfer.setTransferType("Transfer");
        User user = getLoggedUser(principal);
        fundTransfer.setTransactionBy(user);
        fundTransferService.saveFundTransfer(fundTransfer);
        return new ResponseEntity(fundTransfer, HttpStatus.OK);
    }

    @PostMapping("fund-transfer/receipt")
    public ResponseEntity<SavingBankTransaction> createSavingBankTransaction(@Valid @RequestBody final SavingBankTransaction savingBankTransaction, Principal principal) {
        bodDateService.checkBOD();
        SavingBankTransaction persistedDeposit = null;
        SavingBankTransaction latestRecord = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(savingBankTransaction.getAccountNumber());
        BigDecimal latestBalanceAmount = latestRecord.getBalance();
        BigDecimal creditAmount = savingBankTransaction.getCreditAmount();
        if (savingBankTransaction.getTransactionType().equalsIgnoreCase("CREDIT")) {
            savingBankTransaction.setBalance(latestBalanceAmount.add(creditAmount));
            savingBankTransaction.setVoucherType("RECEIPT");
            savingBankTransaction.setDebitAmount(BigDecimal.ZERO);
            persistedDeposit = savingBankTransactionService.createSavingBankTransaction(savingBankTransaction);
            if (persistedDeposit != null) {
                // transactionCreditEntry(savingBankTransaction);
            }
        } else if ((savingBankTransaction.getTransactionType().equalsIgnoreCase("DEBIT") && (savingBankTransaction.getDebitAmount().compareTo(latestRecord.getBalance()) <= 0))) {
            savingBankTransaction.setBalance(latestBalanceAmount.subtract(savingBankTransaction.getDebitAmount()));
            savingBankTransaction.setVoucherType("PAYMENT");
            savingBankTransaction.setCreditAmount(BigDecimal.ZERO);
            persistedDeposit = savingBankTransactionService.createSavingBankTransaction(savingBankTransaction);
            if (persistedDeposit != null) {
                // transactionDebitEntry(savingBankTransaction);
            }
        } else {
            log.warn("Withdraw amount is morethan balance amount");
        }
        return new ResponseEntity<>(persistedDeposit, HttpStatus.CREATED);
    }

    private void transactionCreditEntry(FundTransfer fundTransfer, Principal principal) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Credited to " + fundTransfer.getToAccountType() + " " + fundTransfer.getToAccountNumber());
            creditTransaction.setCreditAmount(fundTransfer.getAmount());
            creditTransaction.setTransactionBy(fundTransfer.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setTransferType("Transfer");
            creditTransaction.setAccountNumber(fundTransfer.getToAccountNumber());
            User user = getLoggedUser(principal);
            creditTransaction.setTransactionBy(user);
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);

            if (fundTransfer.getToAccountType().equalsIgnoreCase("CURRENT_ACCOUNT")) {
                Ledger ledger = accountHeadService.getLedgerByName("Current Account");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            } else if (fundTransfer.getToAccountType().equalsIgnoreCase("SAVING_BANK")) {
                Ledger ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            } else if (fundTransfer.getToAccountType().equalsIgnoreCase("RECURRING_DEPOSIT")) {
                Ledger ledger = accountHeadService.getLedgerByName("Recurring Deposit");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            }
            creditTransaction.setTransactionBy(user);
            transactionService.transactionEntry(creditTransaction);
        }
    }

    private void transactionCreditEntryForSavingBank(FundTransfer fundTransfer, Principal principal, SavingsBankDeposit savingsBankDeposit) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Credited to " + fundTransfer.getToAccountType() + " " + fundTransfer.getToAccountNumber());
            creditTransaction.setCreditAmount(fundTransfer.getAmount());
            creditTransaction.setTransactionBy(fundTransfer.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setTransferType("Transfer");
            creditTransaction.setAccountNumber(fundTransfer.getToAccountNumber());
            User user = getLoggedUser(principal);
            creditTransaction.setTransactionBy(user);
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);

            if (fundTransfer.getToAccountType().equalsIgnoreCase("SAVING_BANK")) {

                Ledger ledger = null;
                if (savingsBankDeposit.getAccountType().equals(AccountType.SAVING)) {
                    ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
                } else {
                    ledger = accountHeadService.getLedgerByName("Current Account");
                }
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }

                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            }
            creditTransaction.setTransactionBy(user);
            transactionService.transactionEntry(creditTransaction);
        }
    }

    private void transactionLoanCreditEntry(FundTransfer fundTransfer, Loan loan, Principal principal) {
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Credited to " + fundTransfer.getToAccountType() + " " + fundTransfer.getToAccountNumber());
            creditTransaction.setCreditAmount(fundTransfer.getAmount());
            creditTransaction.setTransactionBy(fundTransfer.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setTransferType("Transfer");
            creditTransaction.setAccountNumber(fundTransfer.getToAccountNumber());
            User user = getLoggedUser(principal);
            creditTransaction.setTransactionBy(user);
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);

            if (fundTransfer.getToAccountType().equalsIgnoreCase("LOAN")) {
                if (loan.getLoanType() == LoanType.GOLD) {
                    Ledger ledger = accountHeadService.getLedgerByName("Gold");
                    if (ledger == null) {
                        throw new EntityNotFoundException("Account head or ledger not found");
                    }
                    creditTransaction.setLedger(ledger);
                    creditTransaction.setAccountHead(ledger.getAccountHead());
                } else if (loan.getLoanType() == LoanType.LAD) {
                    Ledger ledger = accountHeadService.getLedgerByName("Lad");
                    if (ledger == null) {
                        throw new EntityNotFoundException("Account head or ledger not found");
                    }
                    creditTransaction.setLedger(ledger);
                    creditTransaction.setAccountHead(ledger.getAccountHead());
                } else if (loan.getLoanType() == LoanType.TERM) {
                    Ledger ledger = accountHeadService.getLedgerByName("Term");
                    if (ledger == null) {
                        throw new EntityNotFoundException("Account head or ledger not found");
                    }
                    creditTransaction.setLedger(ledger);
                    creditTransaction.setAccountHead(ledger.getAccountHead());
                }
            }
            creditTransaction.setTransactionBy(user);
            transactionService.transactionEntry(creditTransaction);
        }
    }

    private void transactionDebitEntry(FundTransfer fundTransfer, Principal principal) {
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(fundTransfer.getAmount());
            debitTransaction.setRemark("Debited from " + fundTransfer.getFromAccountType() + " " + fundTransfer.getFromAccountNumber());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(fundTransfer.getTransactionBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setTransferType("Transfer");
            debitTransaction.setAccountNumber(fundTransfer.getFromAccountNumber());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);
            User user = getLoggedUser(principal);
            debitTransaction.setTransactionBy(user);

            if (fundTransfer.getFromAccountType().equalsIgnoreCase("CURRENT_ACCOUNT")) {
                Ledger ledger = accountHeadService.getLedgerByName("Current Account");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("SAVING_BANK")) {
                Ledger ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("CHILDRENS_DEPOSIT")) {
                Ledger ledger = accountHeadService.getLedgerByName("Children Deposit");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("DOUBLE_SCHEME")) {
                Ledger ledger = accountHeadService.getLedgerByName("Double Scheme");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("FIXED_DEPOSIT")) {
                Ledger ledger = accountHeadService.getLedgerByName("Fixed Deposit");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("TERM_DEPOSIT")) {
                Ledger ledger = accountHeadService.getLedgerByName("Term Deposit");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("PIGMY_DEPOSIT")) {
                Ledger ledger = accountHeadService.getLedgerByName("Pigmy Deposit");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (fundTransfer.getFromAccountType().equalsIgnoreCase("RECURRING_DEPOSIT")) {
                Ledger ledger = accountHeadService.getLedgerByName("Recurring Deposit");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            }
            transactionService.transactionEntry(debitTransaction);
        }
    }

    private void transactionDebitEntryForSavingBank(FundTransfer fundTransfer, Principal principal, SavingsBankDeposit savingsBankDeposit) {
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(fundTransfer.getAmount());
            debitTransaction.setRemark("Debited from " + fundTransfer.getFromAccountType() + " " + fundTransfer.getFromAccountNumber());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(fundTransfer.getTransactionBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setTransferType("Transfer");
            debitTransaction.setAccountNumber(fundTransfer.getFromAccountNumber());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);
            User user = getLoggedUser(principal);
            debitTransaction.setTransactionBy(user);

            if (fundTransfer.getFromAccountType().equalsIgnoreCase("SAVING_BANK")) {
                Ledger ledger = null;
                if (savingsBankDeposit.getAccountType().equals(AccountType.SAVING)) {
                    ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
                } else {
                    ledger = accountHeadService.getLedgerByName("Current Account");
                }
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            }
            transactionService.transactionEntry(debitTransaction);
        }
    }

    private SavingBankTransaction savingBankTransactionDebitEntry(FundTransfer fundTransfer, Principal principal) {
        SavingBankTransaction savingBankTransaction = new SavingBankTransaction();
        SavingBankTransaction fromAccountNumberObject = savingBankTransactionService.getLatestTransactionOfSB(fundTransfer.getFromAccountNumber());
        if (fromAccountNumberObject == null) {
            throw new EntityNotFoundException("Latest Transaction not found or Account not approved");
        }
        savingBankTransaction.setBalance(fromAccountNumberObject.getBalance().subtract(fundTransfer.getAmount()));
        savingBankTransaction.setDebitAmount(fundTransfer.getAmount());
        savingBankTransaction.setTransactionType("DEBIT");
        savingBankTransaction.setVoucherType("RECEIPT");
        savingBankTransaction.setSavingsBankDeposit(fromAccountNumberObject.getSavingsBankDeposit());
        savingBankTransaction.setCreditAmount(BigDecimal.ZERO);
        User user = getLoggedUser(principal);
        savingBankTransaction.setTransactionBy(user);
        savingBankTransaction.setAccountNumber(fundTransfer.getFromAccountNumber());
        SavingBankTransaction sbDebitTransaction = savingBankTransactionService.createSavingBankTransaction(savingBankTransaction);
        SavingsBankDeposit sbDebitAccount = savingsBankDepositService.getSavingsBankDepositByAccountNumber(sbDebitTransaction.getAccountNumber());
        sbDebitAccount.setBalance(sbDebitTransaction.getBalance());
        savingsBankDepositService.saveSavingsBankDeposit(sbDebitAccount);
        return sbDebitTransaction;
    }

    private SavingBankTransaction savingBankTransactionCreditEntry(FundTransfer fundTransfer, Principal principal) {
        SavingBankTransaction savingBankTransactionTo = new SavingBankTransaction();
        SavingBankTransaction toAccountNumberObject = savingBankTransactionService.getLatestTransactionOfSB(fundTransfer.getToAccountNumber());
        if (toAccountNumberObject == null) {
            throw new EntityNotFoundException("Latest Transaction not found or Account not approved");
        }
        savingBankTransactionTo.setBalance(toAccountNumberObject.getBalance().add(fundTransfer.getAmount()));
        savingBankTransactionTo.setCreditAmount(fundTransfer.getAmount());
        savingBankTransactionTo.setTransactionType("CREDIT");
        savingBankTransactionTo.setVoucherType("PAYMENT");
        savingBankTransactionTo.setSavingsBankDeposit(toAccountNumberObject.getSavingsBankDeposit());
        savingBankTransactionTo.setDebitAmount(BigDecimal.ZERO);
        User user = getLoggedUser(principal);
        savingBankTransactionTo.setTransactionBy(user);
        savingBankTransactionTo.setAccountNumber(fundTransfer.getToAccountNumber());
        SavingBankTransaction sbCreditTransaction = savingBankTransactionService.createSavingBankTransaction(savingBankTransactionTo);
        SavingsBankDeposit sbCreditAccount = savingsBankDepositService.getSavingsBankDepositByAccountNumber(toAccountNumberObject.getAccountNumber());
        sbCreditAccount.setBalance(sbCreditTransaction.getBalance());
        savingsBankDepositService.saveSavingsBankDeposit(sbCreditAccount);
        return sbCreditTransaction;
    }

    private CurrentAccountTransaction currentAccountDebitEntry(FundTransfer fundTransfer, Principal principal) {

        CurrentAccountTransaction currentAccountTransaction = new CurrentAccountTransaction();
        CurrentAccountTransaction currentAccountLatestBalance = currentAccountTransactionService.getLatestTransactionOfCurrentAccount(fundTransfer.getFromAccountNumber());
        if (currentAccountLatestBalance == null) {
            throw new EntityNotFoundException("Latest Transaction not found or CurrentAccount Account not approved");
        }
        currentAccountTransaction.setBalance(currentAccountLatestBalance.getBalance().subtract(fundTransfer.getAmount()));
        currentAccountTransaction.setDebitAmount(fundTransfer.getAmount());
        currentAccountTransaction.setTransactionType("DEBIT");
        currentAccountTransaction.setVoucherType("RECEIPT");
        currentAccountTransaction.setCurrentAccount(currentAccountLatestBalance.getCurrentAccount());
        currentAccountTransaction.setCreditAmount(BigDecimal.ZERO);
        User user = getLoggedUser(principal);
        currentAccountTransaction.setTransactionBy(user);
        currentAccountTransaction.setAccountNumber(fundTransfer.getFromAccountNumber());
        CurrentAccountTransaction caDebitTransaction = currentAccountTransactionService.createCurrentAccountTransaction(currentAccountTransaction);
        Optional<CurrentAccount> currentAccount = currentAccountService.getCurrentAccountDepositByAccountNumber(caDebitTransaction.getAccountNumber());

        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankDepositByAccountNumber(caDebitTransaction.getAccountNumber());

        savingsBankDeposit.setBalance(caDebitTransaction.getBalance());
        currentAccountService.saveCurrentAccount(currentAccount.get());
        savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);
        return caDebitTransaction;
    }

    private SavingBankTransaction currentAccountCreditEntry(FundTransfer fundTransfer, Principal principal) {
        SavingBankTransaction currentAccountTransaction = new SavingBankTransaction();
        SavingBankTransaction currentAccountLatestBalance = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(fundTransfer.getToAccountNumber());
        if (currentAccountLatestBalance == null) {
            throw new EntityNotFoundException("Latest Transaction not found or CurrentAccount Account not approved");
        }
        currentAccountTransaction.setBalance(currentAccountLatestBalance.getBalance().add(fundTransfer.getAmount()));
        currentAccountTransaction.setDebitAmount(BigDecimal.ZERO);
        currentAccountTransaction.setTransactionType("CREDIT");
        currentAccountTransaction.setVoucherType("PAYMENT");
        currentAccountTransaction.setSavingsBankDeposit(currentAccountLatestBalance.getSavingsBankDeposit());
        currentAccountTransaction.setCreditAmount(fundTransfer.getAmount());
        User user = getLoggedUser(principal);
        currentAccountTransaction.setTransactionBy(user);
        currentAccountTransaction.setAccountNumber(fundTransfer.getToAccountNumber());
        SavingBankTransaction caCreditTransaction = savingBankTransactionService.createSavingBankTransaction(currentAccountTransaction);
        SavingsBankDeposit currentAccount = savingsBankDepositService.getSavingsBankDepositByAccountNumber(caCreditTransaction.getAccountNumber());
        currentAccount.setBalance(caCreditTransaction.getBalance());
        savingsBankDepositService.saveSavingsBankDeposit(currentAccount);
        return caCreditTransaction;
    }

    private ChildrensDeposit childrensDebitEntry(FundTransfer fundTransfer) {
        Optional<ChildrensDeposit> childrensObj = childrensDepositService.getChildrenDepositByAccountNumber(fundTransfer.getFromAccountNumber());
        if (!childrensObj.isPresent()) {
            throw new EntityNotFoundException("ChildrensDeposit Account not found for Account number :" + fundTransfer.getFromAccountNumber());
        }
        ChildrensDeposit childrensDeposit = childrensObj.get();
        if (childrensDeposit.isWithDrawn() == true) {
            throw new EntityNotFoundException("Children Deposit is already withdrawn");
        }
        fundTransfer.setAmount(childrensDeposit.getMaturityAmount());
        childrensDeposit.setMaturityAmount(childrensDeposit.getMaturityAmount().subtract(fundTransfer.getAmount()));
        childrensDeposit.setTransactionType("DEBIT");
        childrensDeposit.setVoucherType("RECEIPT");
        childrensDeposit.setWithDrawn(true);
        ChildrensDeposit childrensWithDraw = childrensDepositService.saveDeposit(childrensDeposit);
        return childrensWithDraw;
    }

    private DoubleScheme doubleSchemeDebitEntry(FundTransfer fundTransfer) {
        Optional<DoubleScheme> doubleSchemeObj = doubleSchemeService.getDoubleSchemeByAccountNumber(fundTransfer.getFromAccountNumber());
        if (!doubleSchemeObj.isPresent()) {
            throw new EntityNotFoundException("DoubleScheme Account not found for Account Number :" + fundTransfer.getFromAccountNumber());
        }
        DoubleScheme doubleScheme = doubleSchemeObj.get();
        fundTransfer.setAmount(doubleScheme.getMaturityAmount());
        doubleScheme.setMaturityAmount(doubleScheme.getMaturityAmount().subtract(fundTransfer.getAmount()));
        doubleScheme.setWithDrawn(true);
        DoubleScheme doubleSchemeDrawn = doubleSchemeService.saveDoubleSchemeDeposit(doubleScheme);
        return doubleSchemeDrawn;
    }

    private FixedDeposit fixedDepositDebitEntry(FundTransfer fundTransfer) {
        Optional<FixedDeposit> fixedDepositObj = fixedDepositService.getFixedDepositByAccountNumber(fundTransfer.getFromAccountNumber());
        if (!fixedDepositObj.isPresent()) {
            throw new EntityNotFoundException("FixedDeposit Account not found for Account number :" + fundTransfer.getFromAccountNumber());
        }
        FixedDeposit fixedDeposit = fixedDepositObj.get();

        fixedDepositService.checkFixedDepositForLoan(fixedDeposit.getAccountNumber());

        if (fixedDeposit.isWithDrawn() == true) {
            throw new EntityNotFoundException("Fixed Deposit is already withdrawn");
        }
        fundTransfer.setAmount(fixedDeposit.getMaturityAmount());
        fixedDeposit.setMaturityAmount(fixedDeposit.getMaturityAmount().subtract(fundTransfer.getAmount()));
        fixedDeposit.setWithDrawn(true);
        FixedDeposit fixedDepositDrawn = fixedDepositService.saveFixedDeposit(fixedDeposit);
        return fixedDepositDrawn;
    }

    private TermDeposit termDepositDebitEntry(FundTransfer fundTransfer) {
        Optional<TermDeposit> termDepositObj = termDepositService.getTermDepositByAccountNumber(fundTransfer.getFromAccountNumber());
        if (!termDepositObj.isPresent()) {
            throw new EntityNotFoundException("TermDeposit Account not found for Account number :" + fundTransfer.getFromAccountNumber());
        }
        TermDeposit termDeposit = termDepositObj.get();
        fundTransfer.setAmount(termDeposit.getMaturityAmount());
        termDeposit.setMaturityAmount(termDeposit.getMaturityAmount().subtract(fundTransfer.getAmount()));
        termDeposit.setWithDrawn(true);
        TermDeposit termDepositDrawn = termDepositService.saveTermDeposit(termDeposit);
        return termDepositDrawn;
    }

    private PigmyDeposit pigmyDepositDebitEntry(FundTransfer fundTransfer) {
        Optional<PigmyDeposit> pigmyDepositObj = pigmyDepositService.getPigmyDepositByAccountNumber(fundTransfer.getFromAccountNumber());
        if (!pigmyDepositObj.isPresent()) {
            throw new EntityNotFoundException("PigmyDeposit Account not found for Account number :" + fundTransfer.getFromAccountNumber());
        }
        PigmyDeposit pigmyDeposit = pigmyDepositObj.get();

        pigmyDepositService.checkPigmyDepositForLoan(pigmyDeposit.getAccountNumber());

        fundTransfer.setAmount(pigmyDeposit.getDepositAmount());
//        pigmyDeposit.setMaturityAmount(pigmyDeposit.getMaturityAmount().subtract(fundTransfer.getAmount()));
        pigmyDeposit.setWithDrawn(true);
        PigmyDeposit pigmyDepositDraw = pigmyDepositService.createPigmyDeposit(pigmyDeposit);
        return pigmyDepositDraw;
    }

    private Loan updateLoanFundTransfer(FundTransfer fundTransfer, Loan loan) {
        BigDecimal repaidAmount = new BigDecimal(0);
        BigDecimal balanceAmount = loan.getLoanDetail().getBalanceAmount().subtract(fundTransfer.getAmount());
        if (loan.getLoanDetail().getRepaidAmount() != null && loan.getLoanDetail().getRepaidAmount().intValue() > 0) {
            repaidAmount = loan.getLoanDetail().getRepaidAmount().add(fundTransfer.getAmount());
        } else {
            repaidAmount = fundTransfer.getAmount();
        }
        loan.getLoanDetail().setBalanceAmount(balanceAmount);
        loan.getLoanDetail().setRepaidAmount(repaidAmount);
        Loan persistedLoan = loanService.applyLean(loan);
        return persistedLoan;
    }

    private void fundTransferLoanTransactionEntry(Loan loan, FundTransfer fundTransfer, User user) {
        LoanTransaction loanTransaction = loanTransactionService.latestLoanTransaction(loan);
        if (loanTransaction != null && fundTransfer != null) {

            LoanTransaction transaction = new LoanTransaction();
            transaction.setLoan(loan);
            transaction.setCreditAmount(fundTransfer.getAmount());
            transaction.setTransactionBy(user);
            transaction.setTransactionOn(DateUtil.getTodayDate());
            transaction.setTransactionType(TransactionType.CREDIT);

            if (loanTransaction != null) {
                BigDecimal balanceAsOf = loanTransaction.getBalance();
                balanceAsOf = balanceAsOf.subtract(fundTransfer.getAmount());
                transaction.setBalance(balanceAsOf);
            }

            loanTransactionService.loanTransactionEntry(transaction);
        }
    }

    private RecurringDepositTransaction recurringDepositTransactionCreditEntry(FundTransfer fundTransfer, Principal principal) {
        RecurringDepositTransaction recurringDepositTransactionTo = new RecurringDepositTransaction();
        RecurringDepositTransaction toAccountNumberObject = recurringDepositTransactionService.getLatestTransactionOfRecurringDepositTransaction(fundTransfer.getToAccountNumber());
        if (toAccountNumberObject == null) {
            throw new EntityNotFoundException("Latest Transaction not found or RD Account not approved");
        }

        recurringDepositService.checkRecurringDepositForLoan(toAccountNumberObject.getAccountNumber());

        recurringDepositTransactionTo.setBalance(toAccountNumberObject.getBalance().add(fundTransfer.getAmount()));
        recurringDepositTransactionTo.setCreditAmount(fundTransfer.getAmount());
        recurringDepositTransactionTo.setTransactionType("CREDIT");
        recurringDepositTransactionTo.setVoucherType("PAYMENT");
        recurringDepositTransactionTo.setRecurringDeposit(toAccountNumberObject.getRecurringDeposit());
        recurringDepositTransactionTo.setDebitAmount(BigDecimal.ZERO);
        User user = getLoggedUser(principal);
        recurringDepositTransactionTo.setTransactionBy(user);
        recurringDepositTransactionTo.setAccountNumber(fundTransfer.getToAccountNumber());


        Optional<RecurringDeposit> rdCreditAccount = recurringDepositService.getRecurringDepositByAccountNumber(recurringDepositTransactionTo.getAccountNumber());
        RecurringDeposit recurringDeposit = rdCreditAccount.get();
        BigDecimal latestBalanceAmount = recurringDeposit.getBalance();
        BigDecimal creditAmount =fundTransfer.getAmount();

        double totalPaidAmount = latestBalanceAmount.doubleValue();
        double totalPrincipleAmount = creditAmount.doubleValue()*recurringDeposit.getNumberOfInstallments().doubleValue();
        if (recurringDeposit.getDepositAmount().intValue() != creditAmount.intValue()) {
            throw new EntityNotFoundException("Installment Amount should be Rs:" + recurringDeposit.getDepositAmount());
        } else if (totalPaidAmount == totalPrincipleAmount) {
            throw new EntityNotFoundException("All Installment are paid Rs:" + totalPaidAmount);
        }
        RecurringDepositTransaction rdCreditTransaction = recurringDepositTransactionService.createRecurringDepositTransaction(recurringDepositTransactionTo);

//        RecurringDeposit recurringDeposit = rdCreditAccount.get();
        latestBalanceAmount = latestBalanceAmount.add(creditAmount);
        recurringDeposit.setBalance(latestBalanceAmount);
        recurringDepositService.saveRecurringDeposit(recurringDeposit);
        return rdCreditTransaction;
    }

    private void recurringDepositTransactionEntry(RecurringDeposit recurringDeposit, FundTransfer fundTransfer) {
        RecurringDepositTransaction debitTransaction = new RecurringDepositTransaction();
        debitTransaction.setDebitAmount(fundTransfer.getAmount());
        debitTransaction.setRemark("Recurring deposit amount debited for Acc. " + recurringDeposit.getAccountNumber());
        debitTransaction.setCreditAmount(new BigDecimal(0));
        debitTransaction.setTransactionBy(recurringDeposit.getTransactionBy());
        debitTransaction.setTransactionOn(DateUtil.getTodayDate());
        debitTransaction.setTransactionType("DEBIT");
        debitTransaction.setTransactionType(recurringDeposit.getTransactionType());
        debitTransaction.setVoucherType(recurringDeposit.getVoucherType());
        debitTransaction.setBalance(recurringDeposit.getBalance());
        debitTransaction.setAccountNumber(recurringDeposit.getAccountNumber());
        debitTransaction.setRecurringDeposit(recurringDeposit);
        debitTransaction.setDepositAmount(recurringDeposit.getDepositAmount());
        debitTransaction.setTotalPrincipleAmount(recurringDeposit.getDepositAmount().multiply(recurringDeposit.getNumberOfInstallments()));
        recurringDepositTransactionService.createRecurringDepositTransaction(debitTransaction);
    }

}