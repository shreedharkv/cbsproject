package com.dfq.coeffi.cbs.report.pigmyReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction.PigmyDepositTransaction;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.dfq.coeffi.cbs.utils.DateUtil.convertToAscii;

@Service
public class PigmyReportServiceImpl implements PigmyReportService {

    @Autowired
    public PigmyReportRepository pigmyReportRepository;

    @Override
    public List<PigmyDeposit> getPigmyDepositApplicatinByDate(Date dateFrom,Date dateTo,String applicationFrom,String applicationTo) {
        return pigmyReportRepository.getPigmyDepositApplicatinByDate(dateFrom,dateTo,applicationFrom,applicationTo);
    }

    @Override
    public List<PigmyDeposit> getPigmyDepositMemberByAccountNumber(String accountNumberFrom,String accountNumberTo) {
        List<PigmyDeposit> pigmyDeposits =pigmyReportRepository.findAll();
        String strFrom = accountNumberFrom;
        String strTo = accountNumberTo;

        BigInteger mIntAccountNumberFrom = convertToAscii(strFrom.toUpperCase());
        BigInteger mIntAccountNumberTo = convertToAscii(strTo.toUpperCase());


        ArrayList<PigmyDeposit> returnDeposits = new ArrayList<>();
        for (PigmyDeposit pigmyDeposit:pigmyDeposits) {
            BigInteger mIntAccountNumber = convertToAscii(pigmyDeposit.getAccountNumber().toUpperCase());
            if(mIntAccountNumber.doubleValue() >= mIntAccountNumberFrom.doubleValue() && mIntAccountNumber.doubleValue() <= mIntAccountNumberTo.doubleValue()  ){
                returnDeposits.add(pigmyDeposit);
            }

        }

        return returnDeposits;
    }


    @Override
    public List<PigmyDepositTransaction> getPigmyDepositByDate(Date inputDate) {
        return pigmyReportRepository.getPigmyDepositByDate(inputDate);
    }

    @Override
    public List<PigmyDepositTransaction> getPigmyDepositByMonth(int inputMonth,String accountNumber) {
        return pigmyReportRepository.getPigmyDepositByMonth(inputMonth, accountNumber);
    }
}
