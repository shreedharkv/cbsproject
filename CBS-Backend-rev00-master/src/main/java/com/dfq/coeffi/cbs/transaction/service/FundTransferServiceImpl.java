package com.dfq.coeffi.cbs.transaction.service;

import com.dfq.coeffi.cbs.transaction.entity.FundTransfer;
import com.dfq.coeffi.cbs.transaction.repository.FundTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FundTransferServiceImpl implements FundTransferService{

    @Autowired
    public FundTransferRepository fundTransferRepository;

    @Override
    public FundTransfer saveFundTransfer(FundTransfer fundTransfer) {
        return fundTransferRepository.save(fundTransfer);
    }

    @Override
    public List<FundTransfer> getAllFundTransfer() {
        return fundTransferRepository.findAll();
    }

    @Override
    public List<FundTransfer> getFundTransferByTransactionDate(Date inputDate) {
        return fundTransferRepository.getFundTransferByTransactionDate(inputDate);
    }
}
