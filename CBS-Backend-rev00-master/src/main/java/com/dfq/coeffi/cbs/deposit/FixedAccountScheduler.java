package com.dfq.coeffi.cbs.deposit;

import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.deposit.service.DepositInterestCalculationService;
import com.dfq.coeffi.cbs.deposit.service.FixedDepositService;
import com.dfq.coeffi.cbs.deposit.service.SavingsBankDepositService;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.master.service.DepositRateOfInterestService;
import com.dfq.coeffi.cbs.member.entity.DividendPayment;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@Slf4j
public class FixedAccountScheduler {

    @Autowired
    public SavingsBankDepositService savingsBankDepositService;

    @Autowired
    public DepositRateOfInterestService depositRateOfInterestService;

    @Autowired
    public DepositInterestCalculationService depositInterestCalculationService;

    @Autowired
    public FixedDepositService fixedDepositService;

    @Autowired
    public AccountHeadService accountHeadService;

    @Autowired
    public SavingBankTransactionService savingBankTransactionService;

    @Autowired
    public BankService bankService;

    @Autowired
    public TransactionService transactionService;

    @Autowired
    public FixedAccountScheduler() {

    }

    // Which calls every 24 hours
    @Scheduled(fixedDelay = 86400000, initialDelay = 86400000)
    public void setFDAccountDailyInterestAmount() {
        List<FixedDeposit> fixedDepositList = fixedDepositService.getActiveFixedDeposits();
        if (CollectionUtils.isEmpty(fixedDepositList)) {
            throw new EntityNotFoundException("No Fixed Deposit Accounts Found");
        }
        for (FixedDeposit fixedDeposit : fixedDepositList) {
            calculateRateOfInterest(fixedDeposit);
        }
    }

    private BigDecimal calculateRateOfInterest(FixedDeposit fixedDeposit) {

        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(fixedDeposit.getMember().getMemberNumber());
        if (savingsBankDeposit == null) {
            throw new EntityNotFoundException("Saving Bank Deposit not found");
        }

        BigDecimal fixedDepositInterestAmount = new BigDecimal(0);

        BigDecimal principalAmount = fixedDeposit.getDepositAmount();
        double value = fixedDeposit.getRateOfInterest();
        BigDecimal rateOfInterest = new BigDecimal(Double.toString(value));
        rateOfInterest = rateOfInterest.divide(BigDecimal.valueOf(100));
        BigDecimal interestAmount;

        BigDecimal amount = principalAmount.multiply(rateOfInterest.add(BigDecimal.valueOf(1)));
        interestAmount = amount.subtract(principalAmount);
        BigDecimal day = new BigDecimal(365);
        BigDecimal interestAmountPerDay = interestAmount.divide(day, RoundingMode.HALF_UP);

        FixedDepositInterestCalculation fixedDepositInterestCalculation = new FixedDepositInterestCalculation();

        fixedDepositInterestCalculation.setAccountNumber(fixedDeposit.getAccountNumber());
        fixedDepositInterestCalculation.setDepositAmount(fixedDeposit.getDepositAmount());
        fixedDepositInterestCalculation.setDepositType(fixedDeposit.getDepositType().name());
        fixedDepositInterestCalculation.setInterestAmount(interestAmountPerDay);
        fixedDepositInterestCalculation.setInterestRate(new Float(fixedDeposit.getRateOfInterest()));
        fixedDepositInterestCalculation.setIsInterestAdded(false);

        FixedDepositInterestCalculation persistedObject = depositInterestCalculationService.saveFixedDepositInterestCalculation(fixedDepositInterestCalculation);

        Date lastDateOfCurrentMonth = DateUtil.getLastDateOfCurrentMonth(persistedObject.getCreatedOn());

        Date startDate = fixedDeposit.getLastUpdatedDate();
        Date endDate = DateUtil.getTodayDate();

        long duration = endDate.getTime() - startDate.getTime();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

        if (fixedDeposit.getInterestCalculationPeriod().equalsIgnoreCase("Monthly") && DateUtil.getTodayDate().equals(lastDateOfCurrentMonth)) {

            List<FixedDepositInterestCalculation> fixedDepositInterestCalculationBetweenDates = depositInterestCalculationService.getFixedDepositInterestCalculationBetweenDates(startDate, lastDateOfCurrentMonth, fixedDeposit.getAccountNumber());
            for (FixedDepositInterestCalculation fixedDepositInterestCalculationObj1 : fixedDepositInterestCalculationBetweenDates) {

                fixedDepositInterestAmount = fixedDepositInterestCalculationObj1.getInterestAmount();

                fixedDepositInterestAmount = fixedDepositInterestAmount.add(fixedDepositInterestAmount);

                fixedDepositInterestCalculationObj1.setIsInterestAdded(true);
                FixedDepositInterestCalculation depositInterestCalculation = depositInterestCalculationService.saveFixedDepositInterestCalculation(fixedDepositInterestCalculationObj1);
            }

            //FIXED DEPOSIT UPDATE
            BigDecimal addedInterestAmount = fixedDeposit.getAddedInterestAmount();
            addedInterestAmount = addedInterestAmount.add(fixedDepositInterestAmount);
            fixedDeposit.setAddedInterestAmount(addedInterestAmount);
            fixedDeposit.setLastUpdatedDate(DateUtil.getTodayDate());
            FixedDeposit fixedDepositPersistedObj = fixedDepositService.saveFixedDeposit(fixedDeposit);

            //SAVING BANK DEPOSIT UPDATE
            BigDecimal balanceAmount = savingsBankDeposit.getBalance();
            balanceAmount = balanceAmount.add(fixedDepositInterestAmount);
            savingsBankDeposit.setBalance(balanceAmount);
            savingsBankDeposit.setFixedDepositInterestAmount(fixedDepositInterestAmount);
            SavingsBankDeposit savingsBankDepositObject = savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

            //SAVING BANK TRANSACTION UPDATE
            SavingBankTransaction savingBankTransaction = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(savingsBankDeposit.getAccountNumber());

            if (savingBankTransaction != null) {

                BigDecimal depositBalance = savingBankTransaction.getBalance();
                depositBalance = depositBalance.add(fixedDepositInterestAmount);

                SavingBankTransaction sbTransaction = new SavingBankTransaction();

                sbTransaction.setTransactionType("CREDIT");
                sbTransaction.setCreditAmount(fixedDepositInterestAmount);
                sbTransaction.setBalance(depositBalance);
                sbTransaction.setDebitAmount(BigDecimal.ZERO);
                sbTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
                sbTransaction.setSavingsBankDeposit(savingsBankDeposit);

                savingBankTransactionService.createSavingBankTransaction(sbTransaction);
            }
            if (savingsBankDepositObject != null) {
                transactionEntry(savingsBankDeposit);
            }
        }
        if (fixedDeposit.getInterestCalculationPeriod().equalsIgnoreCase("Quarterly") && diffInDays == 91) {

            List<FixedDepositInterestCalculation> fixedDepositInterestCalculationBetweenDates = depositInterestCalculationService.getFixedDepositInterestCalculationBetweenDates(startDate, endDate, fixedDeposit.getAccountNumber());
            for (FixedDepositInterestCalculation fixedDepositInterestCalculationObj1 : fixedDepositInterestCalculationBetweenDates) {

                fixedDepositInterestAmount = fixedDepositInterestCalculationObj1.getInterestAmount();

                fixedDepositInterestAmount = fixedDepositInterestAmount.add(fixedDepositInterestAmount);

                fixedDepositInterestCalculationObj1.setIsInterestAdded(true);
                FixedDepositInterestCalculation depositInterestCalculation = depositInterestCalculationService.saveFixedDepositInterestCalculation(fixedDepositInterestCalculationObj1);
            }

            //FIXED DEPOSIT UPDATE
            BigDecimal addedInterestAmount = fixedDeposit.getAddedInterestAmount();
            addedInterestAmount = addedInterestAmount.add(fixedDepositInterestAmount);
            fixedDeposit.setAddedInterestAmount(addedInterestAmount);
            fixedDeposit.setLastUpdatedDate(DateUtil.getTodayDate());
            FixedDeposit fixedDepositPersistedObj = fixedDepositService.saveFixedDeposit(fixedDeposit);

            //SAVING BANK DEPOSIT UPDATE
            BigDecimal balanceAmount = savingsBankDeposit.getBalance();
            balanceAmount = balanceAmount.add(fixedDepositInterestAmount);
            savingsBankDeposit.setBalance(balanceAmount);
            savingsBankDeposit.setFixedDepositInterestAmount(fixedDepositInterestAmount);
            SavingsBankDeposit savingsBankDepositObject = savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

            //SAVING BANK TRANSACTION UPDATE
            SavingBankTransaction savingBankTransaction = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(savingsBankDeposit.getAccountNumber());

            if (savingBankTransaction != null) {

                BigDecimal depositBalance = savingBankTransaction.getBalance();
                depositBalance = depositBalance.add(fixedDepositInterestAmount);

                SavingBankTransaction sbTransaction = new SavingBankTransaction();

                sbTransaction.setTransactionType("CREDIT");
                sbTransaction.setCreditAmount(fixedDepositInterestAmount);
                sbTransaction.setBalance(depositBalance);
                sbTransaction.setDebitAmount(BigDecimal.ZERO);
                sbTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
                sbTransaction.setSavingsBankDeposit(savingsBankDeposit);

                savingBankTransactionService.createSavingBankTransaction(sbTransaction);
            }
            if (savingsBankDepositObject != null) {
                transactionEntry(savingsBankDeposit);
            }
        }

        if (fixedDeposit.getInterestCalculationPeriod().equalsIgnoreCase("Yearly") && diffInDays == 365) {

            List<FixedDepositInterestCalculation> fixedDepositInterestCalculationBetweenDates = depositInterestCalculationService.getFixedDepositInterestCalculationBetweenDates(startDate, endDate, fixedDeposit.getAccountNumber());
            for (FixedDepositInterestCalculation fixedDepositInterestCalculationObj1 : fixedDepositInterestCalculationBetweenDates) {

                fixedDepositInterestAmount = fixedDepositInterestCalculationObj1.getInterestAmount();

                fixedDepositInterestAmount = fixedDepositInterestAmount.add(fixedDepositInterestAmount);

                fixedDepositInterestCalculationObj1.setIsInterestAdded(true);
                FixedDepositInterestCalculation depositInterestCalculation = depositInterestCalculationService.saveFixedDepositInterestCalculation(fixedDepositInterestCalculationObj1);
            }

            //FIXED DEPOSIT UPDATE
            BigDecimal addedInterestAmount = fixedDeposit.getAddedInterestAmount();
            addedInterestAmount = addedInterestAmount.add(fixedDepositInterestAmount);
            fixedDeposit.setAddedInterestAmount(addedInterestAmount);
            fixedDeposit.setLastUpdatedDate(DateUtil.getTodayDate());
            FixedDeposit fixedDepositPersistedObj = fixedDepositService.saveFixedDeposit(fixedDeposit);

            //SAVING BANK DEPOSIT UPDATE
            BigDecimal balanceAmount = savingsBankDeposit.getBalance();
            balanceAmount = balanceAmount.add(fixedDepositInterestAmount);
            savingsBankDeposit.setBalance(balanceAmount);
            savingsBankDeposit.setFixedDepositInterestAmount(fixedDepositInterestAmount);
            SavingsBankDeposit savingsBankDepositObject = savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

            //SAVING BANK TRANSACTION UPDATE
            SavingBankTransaction savingBankTransaction = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(savingsBankDeposit.getAccountNumber());

            if (savingBankTransaction != null) {

                BigDecimal depositBalance = savingBankTransaction.getBalance();
                depositBalance = depositBalance.add(fixedDepositInterestAmount);

                SavingBankTransaction sbTransaction = new SavingBankTransaction();

                sbTransaction.setTransactionType("CREDIT");
                sbTransaction.setCreditAmount(fixedDepositInterestAmount);
                sbTransaction.setBalance(depositBalance);
                sbTransaction.setDebitAmount(BigDecimal.ZERO);
                sbTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
                sbTransaction.setSavingsBankDeposit(savingsBankDeposit);

                savingBankTransactionService.createSavingBankTransaction(sbTransaction);
            }
            if (savingsBankDepositObject != null) {
                transactionEntry(savingsBankDeposit);
            }
        }
        return null;
    }

    // FIXED DEPOSIT INTEREST CALCULATION CREDIT AND DEBIT ENTRY
    private void transactionEntry(SavingsBankDeposit savingsBankDeposit) {

        Ledger debitLedger = accountHeadService.getLedgerByName("Share Capital");

        Ledger creditLedger = accountHeadService.getLedgerByName("Saving Bank Deposit");

        Optional<BankMaster> bankMasterObj = bankService.getActiveBank();
        if (!bankMasterObj.isPresent()) {
            log.warn("No active bank found");
            throw new EntityNotFoundException(BankMaster.class.getName());
        }
        BankMaster bankMaster = bankMasterObj.get();

        // FOR DEBIT TRANSACTION

        Transaction latestDebitTransaction = transactionService.latestTransaction();

        if (latestDebitTransaction != null) {

            Transaction debitTransaction = new Transaction();

            debitTransaction.setDebitAmount(savingsBankDeposit.getFixedDepositInterestAmount());
            debitTransaction.setRemark("Amount debited from society Acc No. : " + bankMaster.getAccountNumber());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setAccountNumber(bankMaster.getAccountNumber());
            debitTransaction.setTransferType("Transfer");
            debitTransaction.setAccountHead(debitLedger.getAccountHead());
            debitTransaction.setLedger(debitLedger);

            BigDecimal balance = latestDebitTransaction.getBalance();
            balance = balance.subtract(savingsBankDeposit.getFixedDepositInterestAmount());
            debitTransaction.setBalance(balance);

            transactionService.transactionEntry(debitTransaction);

            BigDecimal bankBalance = bankMaster.getBalance();
            bankBalance = bankBalance.subtract(savingsBankDeposit.getFixedDepositInterestAmount());
            bankMaster.setBalance(bankBalance);
            bankService.saveBankMaster(bankMaster);
        }

        // FOR CREDIT TRANSACTION
        Transaction latestCreditTransaction = transactionService.latestTransaction();

        if (latestCreditTransaction != null) {

            Transaction creditTransaction = new Transaction();

            creditTransaction.setCreditAmount(savingsBankDeposit.getFixedDepositInterestAmount());
            creditTransaction.setRemark("Amount credited to Saving Bank Acc No. : " + savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransferType("Transfer");
            creditTransaction.setAccountHead(creditLedger.getAccountHead());
            creditTransaction.setLedger(creditLedger);

            BigDecimal balance1 = latestCreditTransaction.getBalance();
            balance1 = balance1.add(savingsBankDeposit.getFixedDepositInterestAmount());
            creditTransaction.setBalance(balance1);

            transactionService.transactionEntry(creditTransaction);

            BigDecimal bankBalance1 = bankMaster.getBalance();
            bankBalance1 = bankBalance1.add(savingsBankDeposit.getFixedDepositInterestAmount());
            bankMaster.setBalance(bankBalance1);
            bankService.saveBankMaster(bankMaster);
        }
    }

}