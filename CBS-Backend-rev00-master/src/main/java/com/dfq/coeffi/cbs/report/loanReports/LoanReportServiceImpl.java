package com.dfq.coeffi.cbs.report.loanReports;

import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class LoanReportServiceImpl implements LoanReportService {

    private final LoanReportRepository loanReportRepository;

    @Autowired
    public LoanReportServiceImpl(LoanReportRepository loanReportRepository) {
        this.loanReportRepository = loanReportRepository;
    }

    @Override
    public List<Loan> getGoldLoanRecoveryDetailsByAccountNumber(String loanAccountNumberFrom, String loanAccountNumberTo, String exgLoanAccountNumberFrom, String exgLoanAccountNumberTo) {
        return loanReportRepository.getGoldLoanRecoveryDetailsByAccountNumber(loanAccountNumberFrom, loanAccountNumberTo, exgLoanAccountNumberFrom, exgLoanAccountNumberTo);
    }

    @Override
    public List<Loan> getGoldLoanRegisterDetailsByAccountNumber(String loanAccountNumberFrom, String loanAccountNumberTo, String exgLoanAccountNumberFrom, String exgLoanAccountNumberTo) {
        return loanReportRepository.getGoldLoanRegisterDetailsByAccountNumber(loanAccountNumberFrom, loanAccountNumberTo, exgLoanAccountNumberFrom, exgLoanAccountNumberTo);
    }

    @Override
    public List<Loan> getAllLoanRecoveryDetail(String loanAccountNumberFrom, String loanAccountNumberTo) {
        return loanReportRepository.findByLoanAccountNumberBetween(loanAccountNumberFrom, loanAccountNumberTo);
    }

    @Override
    public List<Loan> getAllLoanRecoveryDetailByExgAccount(String exgLoanAccountNumberFrom, String exgLoanAccountNumberTo) {
        return loanReportRepository.findByExgLoanAccountNumberBetween(exgLoanAccountNumberFrom, exgLoanAccountNumberTo);
    }
}