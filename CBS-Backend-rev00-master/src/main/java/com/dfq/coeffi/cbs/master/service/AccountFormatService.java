package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormat;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormatType;

import java.util.List;
import java.util.Optional;

public interface AccountFormatService {

    List<AccountFormat> getAccountNumbers();

    AccountFormat saveAccountNumber(AccountFormat accountNumber);

    Optional<AccountFormat> getAccountNumber(long id);

    AccountFormat getAccountFormatByTypeAndSubType(AccountFormatType accountFormatType, String subType);
}