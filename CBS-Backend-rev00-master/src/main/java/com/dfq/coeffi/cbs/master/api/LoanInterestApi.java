package com.dfq.coeffi.cbs.master.api;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.roi.loan.LoanRateOfInterest;
import com.dfq.coeffi.cbs.master.service.LoanRateOfInterestService;
import com.dfq.coeffi.cbs.utils.DateUtil;
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

@RestController
@Slf4j
public class LoanInterestApi extends BaseController {

    @Autowired
    private LoanRateOfInterestService loanRateOfInterestService;

    @PostMapping("loan-interest-chart")
    public ResponseEntity<LoanRateOfInterest> createNewLoanInterestEntry(@RequestBody  final LoanRateOfInterest loanRateOfInterest) {

        loanRateOfInterest.setEffectFrom(DateUtil.getTodayDate());
        LoanRateOfInterest persistedObject = loanRateOfInterestService.saveRateOfInterest(loanRateOfInterest);
        if (persistedObject != null) log.info("New loan interest chart created");
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @GetMapping("loan-interest-chart")
    public ResponseEntity<List<LoanRateOfInterest>> getLoanRateOfInterestCharts() {
        List<LoanRateOfInterest> loanRateOfInterests = loanRateOfInterestService.getLoanInterestTable();
        if (CollectionUtils.isEmpty(loanRateOfInterests)) {
            throw new EntityNotFoundException("No loan interest chart table is available");
        }
        return new ResponseEntity<>(loanRateOfInterests, HttpStatus.OK);
    }
}