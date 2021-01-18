package com.dfq.coeffi.cbs.report.accountCloserReports;


import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.report.depositReports.DepositReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DepositAccountCloserServiceImpl implements DepositAccountCloserService {


    private final DepositAccountCloserRepository depositAccountCloserRepository;

    @Autowired
    public DepositAccountCloserServiceImpl(DepositAccountCloserRepository depositAccountCloserRepository){
        this.depositAccountCloserRepository = depositAccountCloserRepository;
    }


    @Override
    public List<ChildrensDeposit> getAllChildrensDepositAccountCloser(int year) {
        return depositAccountCloserRepository.getAllChildrensDepositAccountCloser(year);
    }

    @Override
    public List<DoubleScheme> getAllDoubleSchemeAccountCloser(int year) {
        return depositAccountCloserRepository.getAllDoubleSchemeAccountCloser(year);
    }

    @Override
    public List<RecurringDeposit> getAllRecurringDepositAccountCloser(int year) {
        return depositAccountCloserRepository.getAllRecurringDepositAccountCloser(year);
    }

    @Override
    public List<TermDeposit> getAllTermDepositAccountCloser(int year) {
        return depositAccountCloserRepository.getAllTermDepositAccountCloser(year);
    }

    @Override
    public List<FixedDeposit> getAllFixedDepositAccountCloser(int year) {
        return depositAccountCloserRepository.getAllFixedDepositAccountCloser(year);
    }

    @Override
    public List<SavingsBankDeposit> getAllSavingsBankAccountCloser(int year) {
        return depositAccountCloserRepository.getAllSavingsBankAccountCloser(year);
    }

    @Override
    public List<CurrentAccount> getAllCurrentAccountCloser(int year) {
        return depositAccountCloserRepository.getAllCurrentAccountCloser(year);
    }

    @Override
    public List<Loan> getAllLoanCloser(int year,LoanType loanType) {
        return depositAccountCloserRepository.getAllLoanCloser(year,loanType);
    }
}
