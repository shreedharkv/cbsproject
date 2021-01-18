package com.dfq.coeffi.cbs.master.api;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.roi.fd.DepositRateOfInterest;
import com.dfq.coeffi.cbs.master.entity.roi.loan.LoanRateOfInterest;
import com.dfq.coeffi.cbs.master.service.DepositRateOfInterestService;
import com.dfq.coeffi.cbs.master.service.LoanRateOfInterestService;
import com.dfq.coeffi.cbs.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@Slf4j
public class DepositInterestApi extends BaseController {

    @Autowired
    private DepositRateOfInterestService depositRateOfInterestService;

    @PostMapping("deposit-interest-chart")
    public ResponseEntity<DepositRateOfInterest> createNewDepositInterestEntry(@RequestBody  final DepositRateOfInterest depositRateOfInterest) {

        depositRateOfInterest.setEffectFrom(DateUtil.getTodayDate());
        depositRateOfInterest.setActive(true);
        DepositRateOfInterest persistedObject = depositRateOfInterestService.saveDepositRateOfInterest(depositRateOfInterest);
        if (persistedObject != null) log.info("New deposit interest chart created");
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @GetMapping("deposit-interest-chart")
    public ResponseEntity<List<DepositRateOfInterest>> getLoanRateOfInterestCharts() {
        List<DepositRateOfInterest> depositRateOfInterests = depositRateOfInterestService.getDepositInterestTable();
        if (CollectionUtils.isEmpty(depositRateOfInterests)) {
            throw new EntityNotFoundException("No deposit interest chart table is available");
        }
        return new ResponseEntity<>(depositRateOfInterests, HttpStatus.OK);
    }

    @DeleteMapping("deposit-interest-chart/{id}")
    public ResponseEntity<DepositRateOfInterest> deleteDepositInterestEntry(@PathVariable long id) {

        DepositRateOfInterest depositRateOfInterest = depositRateOfInterestService.getDepositRateOfInterestById(id);
        depositRateOfInterest.setActive(false);
        DepositRateOfInterest persistedObject = depositRateOfInterestService.saveDepositRateOfInterest(depositRateOfInterest);
        if (persistedObject != null) log.info("New deposit interest chart created");
        return new ResponseEntity<>(persistedObject,HttpStatus.NO_CONTENT);
    }
}