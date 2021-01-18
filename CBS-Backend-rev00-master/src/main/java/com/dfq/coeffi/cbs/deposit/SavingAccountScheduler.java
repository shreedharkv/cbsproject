package com.dfq.coeffi.cbs.deposit;

import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.AccountType;
import com.dfq.coeffi.cbs.deposit.entity.DepositInterestCalculation;
import com.dfq.coeffi.cbs.deposit.entity.DepositType;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import com.dfq.coeffi.cbs.deposit.service.DepositInterestCalculationService;
import com.dfq.coeffi.cbs.deposit.service.SavingsBankDepositService;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.entity.roi.fd.DepositRateOfInterest;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.master.service.DepositRateOfInterestService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
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
public class SavingAccountScheduler {

    @Autowired
    public SavingsBankDepositService savingsBankDepositService;

    @Autowired
    public DepositRateOfInterestService depositRateOfInterestService;

    @Autowired
    public DepositInterestCalculationService depositInterestCalculationService;

    @Autowired
    public AccountHeadService accountHeadService;

    @Autowired
    public SavingBankTransactionService savingBankTransactionService;

    @Autowired
    public BankService bankService;

    @Autowired
    public TransactionService transactionService;

    @Autowired
    public SavingAccountScheduler() {

    }

    // Which calls every 24 hours
    @Scheduled(fixedDelay = 86400000, initialDelay = 86400000)
    public void setSBAccountDailyInterestAmount() {
        List<SavingsBankDeposit> savingsBankDepositList = savingsBankDepositService.getActiveSavingBankDeposit();
        if (CollectionUtils.isEmpty(savingsBankDepositList)) {
            throw new EntityNotFoundException("No Saving Bank Accounts Found");
        }
        for (SavingsBankDeposit savingsBankDeposit : savingsBankDepositList) {

            calculateRateOfInterest(savingsBankDeposit);
        }
    }

    private BigDecimal calculateRateOfInterest(SavingsBankDeposit savingsBankDeposit) {

        BigDecimal depositInterestAmount = new BigDecimal(0);

        DepositRateOfInterest depositRateOfInterest = depositRateOfInterestService.getRateOfInterestByDepositTypeAndStatus(DepositType.SAVING_BANK_DEPOSIT);

        BigDecimal principalAmount = savingsBankDeposit.getBalance();
        float value = depositRateOfInterest.getRegularRateOfInterest();
        BigDecimal rateOfInterest = new BigDecimal(Float.toString(value));
        rateOfInterest = rateOfInterest.divide(BigDecimal.valueOf(100));

        BigDecimal interestAmount;

        BigDecimal amount = principalAmount.multiply(rateOfInterest.add(BigDecimal.valueOf(1)));
        interestAmount = amount.subtract(principalAmount);
        BigDecimal day = new BigDecimal(365);
        BigDecimal interestAmountPerDay = interestAmount.divide(day, RoundingMode.HALF_UP);

        DepositInterestCalculation depositInterestCalculation = new DepositInterestCalculation();

        depositInterestCalculation.setAccountNumber(savingsBankDeposit.getAccountNumber());
        depositInterestCalculation.setDepositAmount(savingsBankDeposit.getDepositAmount());
        depositInterestCalculation.setDepositType(savingsBankDeposit.getDepositType().name());
        depositInterestCalculation.setInterestAmount(interestAmountPerDay);
        depositInterestCalculation.setInterestRate(depositRateOfInterest.getRegularRateOfInterest());
        depositInterestCalculation.setIsInterestAdded(false);

        DepositInterestCalculation persistedObject = depositInterestCalculationService.saveDepositInterestCalculation(depositInterestCalculation);

        if (persistedObject != null) {

            Date startDate = savingsBankDeposit.getLastUpdatedDate();
            Date endDate = DateUtil.getTodayDate();

            long duration = endDate.getTime() - startDate.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

            if(diffInDays == 182){

                List<DepositInterestCalculation> depositInterestCalculations = depositInterestCalculationService.getDepositInterestCalculationBetweenDates(startDate, endDate, savingsBankDeposit.getAccountNumber());
                for (DepositInterestCalculation depositInterestCalculationObj : depositInterestCalculations) {

                    depositInterestAmount = depositInterestCalculationObj.getInterestAmount();

                    depositInterestAmount = depositInterestAmount.add(depositInterestAmount);
                    depositInterestCalculationObj.setIsInterestAdded(true);
                    depositInterestCalculationService.saveDepositInterestCalculation(depositInterestCalculationObj);
                }

                //SAVING BANK DEPOSIT UPDATE
                BigDecimal balanceAmount = savingsBankDeposit.getBalance();
                balanceAmount = balanceAmount.add(depositInterestAmount);
                savingsBankDeposit.setBalance(balanceAmount);
                savingsBankDeposit.setSbInterestAmount(depositInterestAmount);
                savingsBankDeposit.setLastUpdatedDate(DateUtil.getTodayDate());
                SavingsBankDeposit savingsBankDepositObject = savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

                //SAVING BANK TRANSACTION UPDATE
                SavingBankTransaction savingBankTransaction = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(savingsBankDeposit.getAccountNumber());

                if (savingBankTransaction != null) {

                    BigDecimal depositBalance = savingBankTransaction.getBalance();
                    depositBalance = depositBalance.add(depositInterestAmount);

                    SavingBankTransaction sbTransaction = new SavingBankTransaction();

                    sbTransaction.setTransactionType("CREDIT");
                    sbTransaction.setCreditAmount(depositInterestAmount);
                    sbTransaction.setBalance(depositBalance);
                    sbTransaction.setDebitAmount(BigDecimal.ZERO);
                    sbTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
                    sbTransaction.setSavingsBankDeposit(savingsBankDeposit);

                    savingBankTransactionService.createSavingBankTransaction(sbTransaction);
                }
                if(savingsBankDepositObject != null){
                    transactionEntry(savingsBankDeposit);
                }
            }
        }
        return interestAmountPerDay;
    }

    // SAVING BANK DEPOSIT INTEREST CALCULATION CREDIT AND DEBIT ENTRY
    private void transactionEntry(SavingsBankDeposit savingsBankDeposit) {

        Ledger debitLedger = accountHeadService.getLedgerByName("Share Capital");

        Ledger creditLedger = null;
        if(savingsBankDeposit.getAccountType().equals(AccountType.SAVING)){
            creditLedger = accountHeadService.getLedgerByName("Saving Bank Deposit");
        }else{
            creditLedger = accountHeadService.getLedgerByName("Current Account");
        }
        if(creditLedger == null){
            throw new EntityNotFoundException("Account head or ledger not found");
        }

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

            debitTransaction.setDebitAmount(savingsBankDeposit.getSbInterestAmount());
            debitTransaction.setRemark("Amount debited from society Acc No. : " + bankMaster.getAccountNumber());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setAccountNumber(bankMaster.getAccountNumber());
            debitTransaction.setTransferType("Transfer");
            debitTransaction.setAccountHead(debitLedger.getAccountHead());
            debitTransaction.setLedger(debitLedger);

            BigDecimal balance = latestDebitTransaction.getBalance();
            balance = balance.subtract(savingsBankDeposit.getSbInterestAmount());
            debitTransaction.setBalance(balance);

            transactionService.transactionEntry(debitTransaction);

            BigDecimal bankBalance = bankMaster.getBalance();
            bankBalance = bankBalance.subtract(savingsBankDeposit.getSbInterestAmount());
            bankMaster.setBalance(bankBalance);
            bankService.saveBankMaster(bankMaster);
        }

        // FOR CREDIT TRANSACTION
        Transaction latestCreditTransaction = transactionService.latestTransaction();

        if (latestCreditTransaction != null) {

            Transaction creditTransaction = new Transaction();

            creditTransaction.setCreditAmount(savingsBankDeposit.getSbInterestAmount());
            creditTransaction.setRemark("Amount credited to Saving Bank Acc No. : " + savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransferType("Transfer");
            creditTransaction.setAccountHead(creditLedger.getAccountHead());
            creditTransaction.setLedger(creditLedger);

            BigDecimal balance1 = latestCreditTransaction.getBalance();
            balance1 = balance1.add(savingsBankDeposit.getSbInterestAmount());
            creditTransaction.setBalance(balance1);

            transactionService.transactionEntry(creditTransaction);

            BigDecimal bankBalance1 = bankMaster.getBalance();
            bankBalance1 = bankBalance1.add(savingsBankDeposit.getSbInterestAmount());
            bankMaster.setBalance(bankBalance1);
            bankService.saveBankMaster(bankMaster);
        }
    }
}
