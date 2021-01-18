package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import com.dfq.coeffi.cbs.master.repository.FinancialYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class FinancialYearServiceImpl implements FinancialYearService {

    @Autowired
    private FinancialYearRepository financialYearRepository;

    @Override
    public List<FinancialYear> getFinancialYears() {
        return financialYearRepository.findAll();
    }

    @Override
    public FinancialYear getCurrentFinancialYear() {
        return financialYearRepository.findByActive(true);
    }

    @Override
    public FinancialYear createNewFinancialYear(FinancialYear financialYear) {
        return financialYearRepository.save(financialYear);
    }
}