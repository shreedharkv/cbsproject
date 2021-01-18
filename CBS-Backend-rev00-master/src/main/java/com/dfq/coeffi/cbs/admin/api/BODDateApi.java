package com.dfq.coeffi.cbs.admin.api;

import com.dfq.coeffi.cbs.admin.entity.BODDate;
import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Slf4j
@RestController
public class BODDateApi extends BaseController {

    private final BODDateService bodDateService;
    private final ApplicationLogService applicationLogService;

    @Autowired
    private BODDateApi(final BODDateService bodDateService, final ApplicationLogService applicationLogService) {
        this.bodDateService = bodDateService;
        this.applicationLogService = applicationLogService;
    }

    @GetMapping("/bod-date")
    public ResponseEntity<BODDate> getBODDateByStatus() {
        Optional<BODDate> bodDateObj = bodDateService.getBODDateByStatus();
        if (!bodDateObj.isPresent()) {
            throw new EntityNotFoundException("BOD Date not found for Date");
        }
        BODDate bodDate = bodDateObj.get();
        return new ResponseEntity<>(bodDate, HttpStatus.OK);
    }

    @PostMapping("bod-date")
    public ResponseEntity<BODDate> createBODDate(@Valid @RequestBody BODDate bodDate, Principal principal) {
        User loggedUser = getLoggedUser(principal);

        bodDate.setForDay(DateUtil.getTodayDate());
        bodDate.setBodStatus(true);
        BODDate persistedObject = bodDateService.saveBODDate(bodDate);

        if (persistedObject != null && loggedUser != null) {
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "BOD started for the day", "BOD START", loggedUser.getId());
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @DeleteMapping("/bod-date/{id}")
    public ResponseEntity<BODDate> endBODDate(@PathVariable long id, Principal principal) {

        User loggedUser = getLoggedUser(principal);

        Optional<BODDate> bodDateObject = bodDateService.getBODDate(id);
        if (!bodDateObject.isPresent()) {
            log.warn("Unable to deactivate BOD DAte with ID : {} not found", id);
            throw new EntityNotFoundException(BODDate.class.getSimpleName());
        }

        BODDate bodDate = bodDateObject.get();
        bodDate.setBodStatus(false);
        bodDate.setEodDate(DateUtil.getTodayDate());
        BODDate persistedObject = bodDateService.saveBODDate(bodDate);

        if (persistedObject != null && loggedUser != null) {
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "EOD closed for the day", "EOD CLOSE", loggedUser.getId());
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }
}