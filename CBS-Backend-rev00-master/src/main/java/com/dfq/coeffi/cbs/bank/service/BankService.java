package com.dfq.coeffi.cbs.bank.service;

import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import java.util.List;
import java.util.Optional;

public interface BankService {

    List<BankMaster> getBankMasters();

    Optional<BankMaster> getBankMaster(long id);

    BankMaster saveBankMaster(BankMaster bankMaster);

    void deleteBankMaster(long id);

    Optional<BankMaster> getActiveBank();

    Optional<BankMaster> getBankMasterByStatus(long id);

    List<BankMaster> getAllBankMasters();

}
