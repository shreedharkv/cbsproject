package com.dfq.coeffi.cbs.report.depositReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DepositReportServiceImpl implements DepositReportService {

    private final DepositReportRepository depositReportRepository;

    @Autowired
    public DepositReportServiceImpl(DepositReportRepository depositReportRepository){
        this.depositReportRepository = depositReportRepository;
    }
    @Override
    public List<FixedDeposit> getFixedDepositReportByDate(Date inputDate,DepositType depositType,String accountNumberFrom,String accountNumberTo,Date dateFrom,Date dateTo,String accountNumber,Date maturityDateFrom, Date maturityDateTo) {
        return depositReportRepository.getFixedDepositReportByDate(inputDate,DepositType.FIXED_DEPOSIT,accountNumberFrom,accountNumberTo,dateFrom,dateTo,accountNumber,maturityDateFrom,maturityDateTo);
    }


    @Override
    public List<CurrentAccount> getCurrentAccountReportByDate(Date inputDate,DepositType depositType,String accountNumberFrom,String accountNumberTo,Date dateFrom,Date dateTo) {
        return depositReportRepository.getCurrentAccountReportByDate(inputDate,DepositType.CURRENT_ACCOUNT,accountNumberFrom,accountNumberTo,dateFrom,dateTo);
    }

    @Override
    public List<ChildrensDeposit> getChildrensDepositReportByDate(Date inputDate,DepositType depositType,String accountNumberFrom,String accountNumberTo,Date dateFrom,Date dateTo,Date maturityDateFrom, Date maturityDateTo) {
        return depositReportRepository.getChildrensDepositReportByDate(inputDate,DepositType.CHILDRENS_DEPOSIT,accountNumberFrom,accountNumberTo,dateFrom,dateTo,maturityDateFrom,maturityDateTo);
    }

    @Override
    public List<DoubleScheme> getDoubleSchemeReportByDate(Date inputDate,DepositType depositType,String accountNumberFrom,String accountNumberTo,Date dateFrom,Date dateTo,Date maturityDateFrom, Date maturityDateTo) {
        return depositReportRepository.getDoubleSchemeReportByDate(inputDate,DepositType.DOUBLE_SCHEME,accountNumberFrom,accountNumberTo,dateFrom,dateTo,maturityDateFrom,maturityDateTo);
    }

    @Override
    public List<RecurringDeposit> getRecurringDepositReportByDate(Date inputDate,DepositType depositType,String accountNumberFrom,String accountNumberTo,Date dateFrom,Date dateTo,Date maturityDateFrom, Date maturityDateTo) {
        return depositReportRepository.getRecurringDepositReportByDate(inputDate,DepositType.RECURRING_DEPOSIT,accountNumberFrom,accountNumberTo,dateFrom,dateTo,maturityDateFrom,maturityDateTo);
    }

    @Override
    public List<SavingsBankDeposit> getSavingsBankDepositReportByDate(Date inputDate,DepositType depositType) {
        return depositReportRepository.getSavingsBankDepositReportByDate(inputDate,DepositType.SAVING_BANK_DEPOSIT);
    }

    @Override
    public List<TermDeposit> getTermDepositReportByDate(Date inputDate,DepositType depositType,String accountNumberFrom,String accountNumberTo,Date dateFrom,Date dateTo,Date maturityDateFrom, Date maturityDateTo) {
        return depositReportRepository.getTermDepositReportByDate(inputDate,DepositType.TERM_DEPOSIT,accountNumberFrom,accountNumberTo,dateFrom,dateTo,maturityDateFrom,maturityDateTo);
    }

    @Override
    public List<Loan> getLoanReportByDate(Date inputDate) {
        return depositReportRepository.getLoanReportByDate(inputDate);
    }

    @Override
    public List<Loan> getLoanReportByDate(Date dateFrom,Date dateTo) {
        return depositReportRepository.getLoanReportByDate(dateFrom,dateTo);
    }


    @Override
    public List<CurrentAccount> getCurrentAccountApplicationDetails(Date dateFrom,Date dateTo,String applicationFrom,String applicationTo,String accountNumberFrom,String accountNumberTo) {
        return depositReportRepository.getCurrentAccountApplicationDetails(dateFrom,dateTo,applicationFrom,applicationTo,accountNumberFrom,accountNumberTo);
    }

    @Override
    public List<CurrentAccountTransaction> getCurrentAccountLedgerDetails(Date dateFrom,Date dateTo,String accountNumber) {
        return depositReportRepository.getCurrentAccountLedgerDetails(dateFrom,dateTo, accountNumber);
    }

    @Override
    public List<PigmyDeposit> getPigmyDepositReportByDate(Date inputDate, DepositType depositType, String accountNumberFrom, String accountNumberTo, Date dateFrom, Date dateTo, String accountNumber, Date maturityDateFrom, Date maturityDateTo) {
        return  depositReportRepository.getPigmyDepositReportByDate(inputDate,DepositType.PIGMY_DEPOSIT,accountNumberFrom,accountNumberTo,dateFrom,dateTo,accountNumber,maturityDateFrom,maturityDateTo);
    }



    @Override
    public List<SavingsBankDeposit> getSavingBankDepositLedgerReport(DepositType depositType, String accountNumber) {
        return depositReportRepository.getSavingBankDepositLedgerReport(DepositType.SAVING_BANK_DEPOSIT, accountNumber);
    }

    @Override
    public List<ChildrensDeposit> getChildrenDepositLedgerReport(DepositType depositType, String accountNumber) {
        return depositReportRepository.getChildrenDepositLedgerReport(DepositType.CHILDRENS_DEPOSIT, accountNumber);
    }

    @Override
    public List<FixedDeposit> getFixedDepositLedgerReport(DepositType depositType, String accountNumber) {
        return depositReportRepository.getFixedDepositLedgerReport(DepositType.FIXED_DEPOSIT, accountNumber);
    }

    @Override
    public List<CurrentAccount> getCurrentAccountDepositLedgerReport(DepositType depositType, String accountNumber) {
        return depositReportRepository.getCurrentAccountDepositLedgerReport(DepositType.CURRENT_ACCOUNT, accountNumber);
    }

    @Override
    public List<DoubleScheme> getDoubleSchemeDepositLedgerReport(DepositType depositType, String accountNumber) {
        return depositReportRepository.getDoubleSchemeDepositLedgerReport(DepositType.DOUBLE_SCHEME, accountNumber);
    }

    @Override
    public List<PigmyDeposit> getPigmyDepositLedgerReport(DepositType depositType, String accountNumber) {
        return depositReportRepository.getPigmyDepositLedgerReport(DepositType.PIGMY_DEPOSIT, accountNumber);
    }

    @Override
    public List<RecurringDeposit> getRecurringDepositLedgerReport(DepositType depositType, String accountNumber) {
        return depositReportRepository.getRecurringDepositLedgerReport(DepositType.RECURRING_DEPOSIT, accountNumber);
    }

    @Override
    public List<TermDeposit> getTermDepositLedgerReport(DepositType depositType, String accountNumber) {
        return depositReportRepository.getTermDepositLedgerReport(DepositType.TERM_DEPOSIT, accountNumber);
    }

}
