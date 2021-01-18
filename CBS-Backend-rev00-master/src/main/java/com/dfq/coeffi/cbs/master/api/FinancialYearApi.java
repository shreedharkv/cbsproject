package com.dfq.coeffi.cbs.master.api;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import com.dfq.coeffi.cbs.master.service.BranchService;
import com.dfq.coeffi.cbs.master.service.FinancialYearService;
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

@Slf4j
@RestController
public class FinancialYearApi extends BaseController {

    @Autowired
    private FinancialYearService financialYearService;

    @GetMapping("financial-year")
    public ResponseEntity<List<FinancialYear>> getFinancialYears() {
        List<FinancialYear> financialYears = financialYearService.getFinancialYears();
        if (CollectionUtils.isEmpty(financialYears)) {
            throw new EntityNotFoundException("No academic years configured yet");
        }
        return new ResponseEntity<>(financialYears, HttpStatus.OK);
    }

    @PostMapping("financial-year")
    public ResponseEntity<FinancialYear> createNewFinancialYear(@RequestBody  final FinancialYear financialYear) {
        FinancialYear persistedObject = financialYearService.createNewFinancialYear(financialYear);
        if (persistedObject != null) log.info("New financial year created");
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @GetMapping("current-financial-year")
    public ResponseEntity<FinancialYear> getActiveFinancialYear() {
        FinancialYear financialYear = financialYearService.getCurrentFinancialYear();
        if(financialYear == null){
            throw new EntityNotFoundException("No active financial year found");
        }
        return new ResponseEntity<>(financialYear, HttpStatus.OK);
    }
}