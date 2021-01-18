package com.dfq.coeffi.cbs.master.api;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormat;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormatType;
import com.dfq.coeffi.cbs.master.service.AccountFormatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class AccountFormatApi extends BaseController {

    @Autowired
    private AccountFormatService accountFormatService;

    @GetMapping("account-number")
    public ResponseEntity<List<AccountFormat>> getActiveAccountNumbers() {
        List<AccountFormat> accountNumbers = accountFormatService.getAccountNumbers();
        if (CollectionUtils.isEmpty(accountNumbers)) {
            throw new EntityNotFoundException("No active account number not found");
        }
        return new ResponseEntity<>(accountNumbers, HttpStatus.OK);
    }

    @PostMapping("account-number")
    public ResponseEntity<AccountFormat> createNewAccountNumber(@RequestBody final AccountFormat accountNumber) {
        accountNumber.setActive(true);
        AccountFormat persistedObject = accountFormatService.saveAccountNumber(accountNumber);
        if (persistedObject != null) log.info("New account number created");
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @GetMapping("account-number-by-type")
    public ResponseEntity<AccountFormat> getAccountFormatByTypeAndSubType() {
        AccountFormat accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.DEPOSIT, "CHILDREN_DEPOSIT");
        return new ResponseEntity<>(accountFormat, HttpStatus.OK);
    }
}