package com.dfq.coeffi.cbs.loan.service;


import com.dfq.coeffi.cbs.loan.entity.LoanAccountNumberFormat;
import com.dfq.coeffi.cbs.loan.repository.LoanAccountNumberFormatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class LoanAccountNumberFormatServiceImpl implements LoanAccountNumberFormatService{

    @Autowired
    private LoanAccountNumberFormatRepository loanAccountNumberFormatRepository;

    @Override
    public LoanAccountNumberFormat getAccountNumberFormat() {
        return loanAccountNumberFormatRepository.findByActive(true);
    }

    @Override
    public LoanAccountNumberFormat updateLoanAccountNumber(LoanAccountNumberFormat loanAccountNumberFormat) {
        return loanAccountNumberFormatRepository.save(loanAccountNumberFormat);
    }
}