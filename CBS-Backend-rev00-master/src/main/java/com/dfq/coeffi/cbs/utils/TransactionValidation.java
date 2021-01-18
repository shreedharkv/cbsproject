package com.dfq.coeffi.cbs.utils;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

public class TransactionValidation {

    public static void checkSocietyBalance(BigDecimal debitAmount, BigDecimal societyBalance){

        if(debitAmount.intValue() > societyBalance.intValue()){
            throw new EntityNotFoundException("Not enough money in society account for this transaction");
        }
    }
}