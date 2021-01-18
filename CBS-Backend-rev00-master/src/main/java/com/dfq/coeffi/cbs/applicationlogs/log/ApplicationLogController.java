package com.dfq.coeffi.cbs.applicationlogs.log;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

@RestController
public class ApplicationLogController extends BaseController {

    @Autowired
    private final ApplicationLogService applicationLogService;

    @Autowired
    public ApplicationLogController(ApplicationLogService applicationLogService) {
        this.applicationLogService = applicationLogService;
    }

    @GetMapping("/logs")
    public ResponseEntity<List<ApplicationLog>> listOfLogs() {
        List<ApplicationLog> logs = applicationLogService.listAllApplicationLogs();
        if (CollectionUtils.isEmpty(logs)) {
            throw new EntityNotFoundException("application-logs");
        }
        Collections.reverse(logs);

        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @GetMapping("/logs/today")
    public ResponseEntity<List<ApplicationLog>> listOfTodayLogs() {
        List<ApplicationLog> logs = applicationLogService.getApplicationLogsByDate(DateUtil.getTodayDate());
        if (CollectionUtils.isEmpty(logs)) {
            throw new EntityNotFoundException("application-logs");
        }
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @GetMapping("/logs/user")
    public ResponseEntity<List<ApplicationLog>> listOfUserLogs() {
        List<ApplicationLog> logs = applicationLogService.getApplicationLogsByDate(DateUtil.getTodayDate());
        if (CollectionUtils.isEmpty(logs)) {
            throw new EntityNotFoundException("application-logs");
        }
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
}