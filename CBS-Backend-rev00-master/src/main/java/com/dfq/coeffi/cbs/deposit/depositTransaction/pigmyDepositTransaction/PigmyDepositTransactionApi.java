package com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction;

import com.dfq.coeffi.cbs.deposit.Dto.PigmyDepositDto;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import com.dfq.coeffi.cbs.deposit.service.PigmyDepositService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dfq.coeffi.cbs.utils.EmiCalculator.pigmyInterestCalculation;

@Transactional
@RestController
@Slf4j
public class PigmyDepositTransactionApi extends BaseController {

    @Autowired
    private PigmyDepositTransactionService pigmyDepositTransactionService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private PigmyDepositService pigmyDepositService;

    @PostMapping("pigmy-deposit-transaction")
    public ResponseEntity<PigmyDepositTransaction> createPigmyDepositTransaction(@Valid @RequestBody final PigmyDepositTransaction pigmyDepositTransaction,Principal principal) {
        PigmyDepositTransaction persistedDeposit = null;
        PigmyDepositTransaction latestRecord= pigmyDepositTransactionService.getLatestTransaction(pigmyDepositTransaction.getPigmyDeposit().getAccountNumber());
        BigDecimal latestBalanceAmount=latestRecord.getBalance();
        BigDecimal creditAmount= pigmyDepositTransaction.getCreditAmount();
        if(pigmyDepositTransaction.getTransactionType().equalsIgnoreCase("CREDIT")) {
            pigmyDepositTransaction.setBalance(latestBalanceAmount.add(creditAmount));
            pigmyDepositTransaction.setVoucherType("RECEIPT");
            pigmyDepositTransaction.setDebitAmount(BigDecimal.ZERO);
            persistedDeposit = pigmyDepositTransactionService.createPigmyDepositTransaction(pigmyDepositTransaction);
            if(persistedDeposit!=null) {
                Transaction transaction = new Transaction();
                transaction.setBalance(pigmyDepositTransaction.getBalance());
                transaction.setCreditAmount(pigmyDepositTransaction.getCreditAmount());
                transaction.setCreatedOn(pigmyDepositTransaction.getCreatedOn());
                transaction.setTransactionType(pigmyDepositTransaction.getTransactionType());
                transaction.setTransactionBy(pigmyDepositTransaction.getTransactionBy());
                transaction.setVoucherType(pigmyDepositTransaction.getVoucherType());
                transaction.setDebitAmount(pigmyDepositTransaction.getDebitAmount());
                transaction.setParticulars("PIGMY DEPOSIT");
                transaction.setTransactionOn(pigmyDepositTransaction.getCreatedOn());
                transaction.setTransferType("Cash");
                transactionService.transactionEntry(transaction);
            }
        }else if((pigmyDepositTransaction.getTransactionType().equalsIgnoreCase("DEBIT" ) && ( pigmyDepositTransaction.getDebitAmount().compareTo(latestRecord.getBalance()) < 0 ))) {
            pigmyDepositTransaction.setBalance(latestBalanceAmount.subtract(pigmyDepositTransaction.getDebitAmount()));
            pigmyDepositTransaction.setVoucherType("PAYMENT");
            pigmyDepositTransaction.setCreditAmount(BigDecimal.ZERO);
            persistedDeposit = pigmyDepositTransactionService.createPigmyDepositTransaction(pigmyDepositTransaction);
            if(persistedDeposit!=null) {
                Transaction transaction = new Transaction();
                transaction.setBalance(pigmyDepositTransaction.getBalance());
                transaction.setDebitAmount(pigmyDepositTransaction.getDebitAmount());
                transaction.setCreatedOn(pigmyDepositTransaction.getCreatedOn());
                transaction.setTransactionType(pigmyDepositTransaction.getTransactionType());
                transaction.setTransactionBy(pigmyDepositTransaction.getTransactionBy());
                transaction.setVoucherType(pigmyDepositTransaction.getVoucherType());
                transaction.setCreditAmount(pigmyDepositTransaction.getCreditAmount());
//              transaction.setAccountName(pigmyDepositTransaction.getPigmyDeposit().getMember().getName());
                transaction.setParticulars("PIGMY DEPOSIT");
                transaction.setTransactionOn(pigmyDepositTransaction.getCreatedOn());
                transaction.setTransferType("Cash");
                transactionService.transactionEntry(transaction);
            }
        }else if( pigmyDepositTransaction.getDebitAmount().compareTo(latestRecord.getBalance()) > 0 )
        {
            log.warn("Withdraw amount is morethan balance amount");
        }
        return new ResponseEntity<>(persistedDeposit,HttpStatus.CREATED);
    }

    @GetMapping("/pigmy-deposit-transactions/{accountNumber}")
    public ResponseEntity<List<PigmyDepositTransaction>> getPigmyDepositTransactions(@PathVariable String accountNumber) {
        List<PigmyDepositTransaction> pigmyDepositTransactions = null;
        Optional<PigmyDeposit> pigmyDeposit = pigmyDepositService.getPigmyDepositByAccountNumber(accountNumber);
        if(pigmyDeposit != null){
            pigmyDepositTransactions = pigmyDepositTransactionService.getPigmyDepositTransactions(pigmyDeposit.get());
        }
        if (CollectionUtils.isEmpty(pigmyDepositTransactions)) {
            throw new EntityNotFoundException("No transactions found for this account");
        }
        return new ResponseEntity<>(pigmyDepositTransactions, HttpStatus.OK);
    }

    @GetMapping("/pigmy-deposit-transactions/interest-calculation/{accountNumber}")
    public ResponseEntity<PigmyDeposit> pigmyInterestCalulation(@PathVariable String accountNumber) throws ParseException {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        List<PigmyDepositTransaction> pigmyCollectionByYear = new ArrayList<>();
        List<PigmyDepositTransaction> pigmyCollectionByMonth = new ArrayList<>();
        BigDecimal principal = BigDecimal.ZERO;
        double interestAmount = 0.0;
        List<PigmyDepositTransaction> pigmyDepositTransaction = pigmyDepositTransactionService.getAllPigmyDepositTransactions(accountNumber);
        Optional<PigmyDeposit> pigmyDepositObj = pigmyDepositService.getPigmyDepositByAccountNumberByStatus(accountNumber);
        PigmyDeposit pigmyDeposit = pigmyDepositObj.get();
        if (!(pigmyDepositTransaction != null && pigmyDepositTransaction.size() > 0)) {
            throw new EntityNotFoundException("No pigmy deposit transaction for account: " + accountNumber);
        }
        for (int month = 1; month <= 12; month++) {
            for (PigmyDepositTransaction depositTransaction : pigmyDepositTransaction) {
                String monthName = monthFormat.format(depositTransaction.getCreatedOn());
                if (month == Integer.parseInt(monthName)) {
                    pigmyCollectionByMonth.add(depositTransaction);
                }
            }
            System.out.println("month: "+month+" Installments: "+pigmyCollectionByMonth.size());
            if (pigmyCollectionByMonth.size() > 4) {
                for (PigmyDepositTransaction pigmyMonthwise : pigmyCollectionByMonth) {
                    pigmyCollectionByYear.add(pigmyMonthwise);
                }
            }
            pigmyCollectionByMonth.clear();
        }
        for (PigmyDepositTransaction pigmyDepositTransactionYear : pigmyCollectionByYear) {
            principal = principal.add(pigmyDepositTransactionYear.getCreditAmount());
        }
        PigmyDepositDto pigmyDepositDto = new PigmyDepositDto();
        if (pigmyDeposit.getMaturityDate().before(DateUtil.getTodayDate()) || (pigmyDeposit.getMaturityDate().equals(DateUtil.getTodayDate()))) {
            interestAmount = pigmyInterestCalculation(principal.doubleValue(), 5, pigmyDeposit.getPeriodOfDeposit().doubleValue());
            pigmyDepositDto.setMaturityDate(DateUtil.getTodayDate());
            int periodOfDeposit = DateUtil.calculateYearsBetweenDate(pigmyDeposit.getCreatedOn(), DateUtil.getTodayDate());
            pigmyDepositDto.setPeriodOfDeposit(BigDecimal.valueOf(periodOfDeposit));
        }
        double periodOfDeposit = DateUtil.calculateDaysBetweenDate(pigmyDeposit.getCreatedOn(), DateUtil.getTodayDate());
        periodOfDeposit = periodOfDeposit/365;
        System.out.println("Period : "+periodOfDeposit);
        interestAmount = pigmyInterestCalculation(principal.doubleValue(), 3, periodOfDeposit);
        pigmyDepositDto.setAccountNumber(accountNumber);
        pigmyDepositDto.setInterestAmount(BigDecimal.valueOf(interestAmount));
        pigmyDepositDto.setPrincipalAmount(principal);
        BigDecimal maturityAmount = principal.add(BigDecimal.valueOf(interestAmount));
        pigmyDepositDto.setMaturityAmount(maturityAmount);
        pigmyDepositDto.setMaturityDate(DateUtil.getTodayDate());
        pigmyDepositDto.setPeriodOfDeposit(BigDecimal.valueOf(periodOfDeposit));
        return new ResponseEntity(pigmyDepositDto, HttpStatus.OK);
    }
}
