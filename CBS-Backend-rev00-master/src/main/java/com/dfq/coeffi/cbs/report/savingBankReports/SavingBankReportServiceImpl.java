package com.dfq.coeffi.cbs.report.savingBankReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import com.dfq.coeffi.cbs.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dfq.coeffi.cbs.utils.DateUtil.convertToAscii;

@Service
public class SavingBankReportServiceImpl implements SavingBankReportService{

    @Autowired
    private SavingBankReportRepository savingBankReportRepository;

    @Override
    public List<SavingsBankDeposit> getSavingBankApplicationDetails(Date dateFrom,Date dateTo,String applicationNumberFrom,String applicationNumberTo) {
        return savingBankReportRepository.getSavingBankApplicationDetails(dateFrom,dateTo,applicationNumberFrom,applicationNumberTo);
    }

    @Override
    public List<SavingsBankDeposit> getSavingBankMemberDetails(String accountNumberFrom,String accountNumberTo) {
        List<SavingsBankDeposit> savingsBankDeposits= savingBankReportRepository.findAll();

        BigInteger mIntAccountNumberFrom = convertToAscii(accountNumberFrom.toUpperCase());
        BigInteger mIntAccountNumberTo = convertToAscii(accountNumberTo.toUpperCase());

        ArrayList<SavingsBankDeposit> returnDeposits = new ArrayList<>();
        for (SavingsBankDeposit savingsBankDeposit:savingsBankDeposits) {

            BigInteger mIntAccountNumber = convertToAscii(savingsBankDeposit.getAccountNumber().toUpperCase());
            if(mIntAccountNumber.doubleValue() >= mIntAccountNumberFrom.doubleValue() && mIntAccountNumber.doubleValue() <= mIntAccountNumberTo.doubleValue()  ){
                returnDeposits.add(savingsBankDeposit);
            }
        }
        return returnDeposits;
//      return savingBankReportRepository.getSavingBankMemberDetails(accountNumberFrom,accountNumberTo);
    }

    @Override
    public List<SavingBankTransaction> getSBLedgerDetails(String accountNumber,Date dateFrom,Date dateTo) {
        return savingBankReportRepository.getSBLedgerDetails(accountNumber,dateFrom,dateTo);
    }
}
