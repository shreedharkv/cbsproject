package com.dfq.coeffi.cbs.report.dashboardReport;


import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanInstallments;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements  DashboardService {

    @Autowired
    public DashboardRepository dashboardRepository;


    @Override
    public List<Transaction> getCashInHand() {
        return dashboardRepository.getCashInHand();
    }

    @Override
    public List<LoanInstallments> getNumberOfLoanInstallmentsDuesInCurrentWeek() {
        return dashboardRepository.getNumberOfLoanInstallmentsDuesInCurrentWeek();
    }

    @Override
    public List<FixedDeposit> getNumberOfDepositsRenewal() {
        return dashboardRepository.getNumberOfDepositsRenewal();
    }

    @Override
    public List<ChildrensDeposit> getNumberOfChildrensDepositsRenewal() {
        return dashboardRepository.getNumberOfChildrensDepositsRenewal();
    }

    @Override
    public List<DoubleScheme> getNumberOfDoubleSchemesRenewal() {
        return dashboardRepository.getNumberOfDoubleSchemesRenewal();
    }

    @Override
    public List<RecurringDeposit> getNumberOfRecurringDepositsRenewal() {
        return dashboardRepository.getNumberOfRecurringDepositsRenewal();
    }

    @Override
    public List<TermDeposit> getNumberOfTermDepositsRenewal() {
        return dashboardRepository.getNumberOfTermDepositsRenewal();
    }

    @Override
    public List<SavingsBankDeposit> getNumberOfSavingBankAccounts() {
        return dashboardRepository.getNumberOfSavingBankAccounts();
    }

    @Override
    public List<ChildrensDeposit> getAllApprovedDeposits() {
        return dashboardRepository.getAllApprovedDeposits();
    }

    @Override
    public List<DoubleScheme> getAllApprovedDoubleScheme() {
        return dashboardRepository.getAllApprovedDoubleScheme();
    }

    @Override
    public List<FixedDeposit> getAllApprovedFixedDeposit() {
        return dashboardRepository.getAllApprovedFixedDeposit();
    }

    @Override
    public List<TermDeposit> getAllApprovedTermDeposit() {
        return dashboardRepository.getAllApprovedTermDeposit();
    }

    @Override
    public List<RecurringDeposit> getAllApprovedRecurringDeposit() {
        return dashboardRepository.getAllApprovedRecurringDeposit();
    }
}
