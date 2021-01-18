package com.dfq.coeffi.cbs.transaction.service;

import com.dfq.coeffi.cbs.transaction.entity.FundTransfer;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FundTransferService {

    FundTransfer saveFundTransfer(FundTransfer fundTransfer);

    List<FundTransfer> getAllFundTransfer();

    List<FundTransfer> getFundTransferByTransactionDate(Date inputDate);
}
