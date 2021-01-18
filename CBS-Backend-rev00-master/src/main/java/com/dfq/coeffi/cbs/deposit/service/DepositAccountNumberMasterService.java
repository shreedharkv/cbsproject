package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.DepositAccountNumberMaster;

import java.util.Optional;

public interface DepositAccountNumberMasterService {

    Optional<DepositAccountNumberMaster> getDepositAccountNumberMasterById(long id);
    DepositAccountNumberMaster saveDepositAccountNumberMaster(DepositAccountNumberMaster depositAccountNumberMaster);
}
