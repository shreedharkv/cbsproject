package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.deposit.entity.DepositType;
import com.dfq.coeffi.cbs.master.entity.roi.fd.DepositRateOfInterest;
import com.dfq.coeffi.cbs.master.repository.DepositRateOfInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DepositRateOfInterestServiceImpl implements DepositRateOfInterestService {

    @Autowired
    private DepositRateOfInterestRepository depositRateOfInterestRepository;

    @Override
    public List<DepositRateOfInterest> getDepositInterestTable() {
        return depositRateOfInterestRepository.findByActive(true);
    }

    @Override
    public DepositRateOfInterest getDepositRateOfInterestById(long id) {
        return depositRateOfInterestRepository.findOne(id);
    }

    @Override
    public DepositRateOfInterest getRateOfInterestByDepositTypeAndStatus(DepositType depositType) {
        return depositRateOfInterestRepository.findByDepositTypeAndActive(depositType, true);
    }

    @Override
    public DepositRateOfInterest saveDepositRateOfInterest(DepositRateOfInterest depositRateOfInterest) {
        return depositRateOfInterestRepository.save(depositRateOfInterest);
    }
}
