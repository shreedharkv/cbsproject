package com.dfq.coeffi.cbs.bank.service;

import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.bank.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Service
public class BankServiceImpl implements BankService{

    @Autowired
    private BankRepository bankRepository;

    @Override
    public List<BankMaster> getBankMasters() {
        return bankRepository.findByActive(true);
    }

    @Override
    public Optional<BankMaster> getBankMaster(long id) {
        return ofNullable(bankRepository.getOne(id));
    }

    @Override
    public BankMaster saveBankMaster(BankMaster bankMaster) {
        return bankRepository.save(bankMaster);
    }

    @Override
    public void deleteBankMaster(long id) {
        bankRepository.delete(id);
    }

    @Override
    public Optional<BankMaster> getActiveBank() {
        return ofNullable(bankRepository.getActiveBank());
    }

    @Override
    public Optional<BankMaster> getBankMasterByStatus(long id) {
        return ofNullable(bankRepository.getBankByStatus(id));
    }

    @Override
    public List<BankMaster> getAllBankMasters() {
        return bankRepository.findAll();
    }
}