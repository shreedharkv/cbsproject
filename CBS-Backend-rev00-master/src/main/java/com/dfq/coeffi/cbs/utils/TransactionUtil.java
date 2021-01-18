package com.dfq.coeffi.cbs.utils;

import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.bank.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;

@Component
public class TransactionUtil {

    private final BankService bankService;

    @Autowired
    public TransactionUtil(final BankService bankService){
        this.bankService = bankService;
    }

    public BankMaster getUpdateSocietyBalance(BigDecimal amount, String transactionType) {
        Optional<BankMaster> bankMasterObj = bankService.getActiveBank();
        if(!bankMasterObj.isPresent()){
            throw new EntityNotFoundException("No active bank found");
        }
        BankMaster bankMaster = bankMasterObj.get();
        if(bankMaster != null && amount.intValue() >0 && transactionType != null){
            BigDecimal balance = bankMaster.getBalance();
            if(transactionType.equalsIgnoreCase("CREDIT")){
                balance = balance.add(amount);
            }else if(transactionType.equalsIgnoreCase("DEBIT")){
                balance = balance.subtract(amount);
            }
            bankMaster.setBalance(balance);
            bankService.saveBankMaster(bankMaster);
        }
        return bankMaster;
    }
}
