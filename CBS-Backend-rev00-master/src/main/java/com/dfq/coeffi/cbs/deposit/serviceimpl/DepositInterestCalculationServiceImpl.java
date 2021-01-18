package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.deposit.entity.DepositInterestCalculation;
import com.dfq.coeffi.cbs.deposit.entity.FixedDepositInterestCalculation;
import com.dfq.coeffi.cbs.deposit.repository.ChildrensDepositRepository;
import com.dfq.coeffi.cbs.deposit.repository.DepositInterestCalculationRepository;
import com.dfq.coeffi.cbs.deposit.repository.FixedDepositInterestCalculationRepository;
import com.dfq.coeffi.cbs.deposit.service.DepositInterestCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DepositInterestCalculationServiceImpl implements DepositInterestCalculationService {

    private final DepositInterestCalculationRepository depositInterestCalculationRepository;
    private final FixedDepositInterestCalculationRepository fixedDepositInterestCalculationRepository;

    @Autowired
    public DepositInterestCalculationServiceImpl(DepositInterestCalculationRepository depositInterestCalculationRepository,
                                                 FixedDepositInterestCalculationRepository fixedDepositInterestCalculationRepository) {
        this.depositInterestCalculationRepository = depositInterestCalculationRepository;
        this.fixedDepositInterestCalculationRepository = fixedDepositInterestCalculationRepository;
    }

    @Override
    public List<DepositInterestCalculation> getDepositInterestCalculations() {
        return depositInterestCalculationRepository.findAll();
    }

    @Override
    public DepositInterestCalculation saveDepositInterestCalculation(DepositInterestCalculation depositInterestCalculation) {
        return depositInterestCalculationRepository.save(depositInterestCalculation);
    }

    @Override
    public List<DepositInterestCalculation> getDepositInterestCalculationBetweenDates(Date dateFrom, Date dateTo, String accountNumber) {
        return depositInterestCalculationRepository.getDepositInterestCalculationBetweenDate(dateFrom, dateTo, accountNumber);
    }

    @Override
    public List<DepositInterestCalculation> getDepositInterestCalculationByAccountNumber(String accountNumber) {
        return depositInterestCalculationRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public List<FixedDepositInterestCalculation> getFixedDepositInterestCalculations() {
        return fixedDepositInterestCalculationRepository.findAll();
    }

    @Override
    public FixedDepositInterestCalculation saveFixedDepositInterestCalculation(FixedDepositInterestCalculation fixedDepositInterestCalculation) {
        return fixedDepositInterestCalculationRepository.save(fixedDepositInterestCalculation);
    }

    @Override
    public List<FixedDepositInterestCalculation> getFixedDepositInterestCalculationBetweenDates(Date dateFrom, Date dateTo, String accountNumber) {
        return fixedDepositInterestCalculationRepository.getFixedDepositInterestCalculationBetweenDate(dateFrom, dateTo, accountNumber);
    }
}