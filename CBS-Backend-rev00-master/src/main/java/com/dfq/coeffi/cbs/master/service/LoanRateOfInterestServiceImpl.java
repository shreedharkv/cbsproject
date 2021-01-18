package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.master.entity.roi.loan.LoanRateOfInterest;
import com.dfq.coeffi.cbs.master.repository.LoanRateOfInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanRateOfInterestServiceImpl implements LoanRateOfInterestService {

    @Autowired
    private LoanRateOfInterestRepository loanRateOfInterestRepository;

    @Override
    public List<LoanRateOfInterest> getLoanInterestTable() {
        return loanRateOfInterestRepository.findAll();
    }

    @Override
    public LoanRateOfInterest getRateOfInterestById(long id) {
        return loanRateOfInterestRepository.findOne(id);
    }

    @Override
    public LoanRateOfInterest getRateOfInterestByLoanTypeAndStatus(LoanType loanType) {
        return loanRateOfInterestRepository.findByLoanTypeAndActive(loanType, true);
    }

    @Override
    public LoanRateOfInterest saveRateOfInterest(LoanRateOfInterest loanRateOfInterest) {
        return loanRateOfInterestRepository.save(loanRateOfInterest);
    }
}