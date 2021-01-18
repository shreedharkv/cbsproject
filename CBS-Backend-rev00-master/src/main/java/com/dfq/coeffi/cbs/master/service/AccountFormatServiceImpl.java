package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormat;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormatType;
import com.dfq.coeffi.cbs.master.repository.AccountFormatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Service
public class AccountFormatServiceImpl implements AccountFormatService {

    @Autowired
    private AccountFormatRepository accountNumberRepository;

    @Override
    public List<AccountFormat> getAccountNumbers() {
        return accountNumberRepository.findByActive(true);
    }

    @Override
    public AccountFormat saveAccountNumber(AccountFormat accountNumber) {
        return accountNumberRepository.save(accountNumber);
    }

    @Override
    public Optional<AccountFormat> getAccountNumber(long id) {
        return ofNullable(accountNumberRepository.findOne(id));
    }

    @Override
    public AccountFormat getAccountFormatByTypeAndSubType(AccountFormatType accountFormatType, String subType) {
        return accountNumberRepository.findByAccountFormatTypeAndSubType(accountFormatType, subType);
    }
}