package com.dfq.coeffi.cbs.deposit.api;

import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;
import com.dfq.coeffi.cbs.deposit.service.PigmyAgentService;
import com.dfq.coeffi.cbs.deposit.service.SavingsBankDepositService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class PigmyAgentApi extends BaseController {


    private final PigmyAgentService pigmyAgentService;

    private final ApplicationLogService applicationLogService;


    @Autowired
    public PigmyAgentApi(PigmyAgentService pigmyAgentService,ApplicationLogService applicationLogService){
        this.pigmyAgentService = pigmyAgentService;
        this.applicationLogService = applicationLogService;
    }

    @GetMapping("/pigmy-agent")
    public ResponseEntity<List<PigmyAgent>> getPigmyAgentDetails() {
        List<PigmyAgent> pigmyAgentDetails = pigmyAgentService.getAllPigmyAgent();
        if (CollectionUtils.isEmpty(pigmyAgentDetails)) {
            log.warn("No PigmyAgentDetails found");
            throw new EntityNotFoundException("No PigmyAgentDetails found");
        }
        return new ResponseEntity<>(pigmyAgentDetails, HttpStatus.OK);
    }

    @PostMapping("/pigmy-agent")
    public ResponseEntity<PigmyAgent> createPigmyAgentDetails(@RequestBody PigmyAgent pigmyAgentDetails, Principal principal) {
        PigmyAgent persistedObject = pigmyAgentService.savePigmyAgent(pigmyAgentDetails);
        if (persistedObject != null) {
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "PigmyAgentDetails created",
                    "PigmyAgentDetails created POST method", loggedUser.getId());
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }

    @GetMapping("/pigmy-agent/{id}")
    public ResponseEntity<PigmyAgent> getPigmyAgentDetails(@PathVariable long id) {
        Optional<PigmyAgent> pigmyAgentDetailsObj = pigmyAgentService.getPigmyAgentById(id);
        if (!pigmyAgentDetailsObj.isPresent()) {
            log.warn("No PigmyAgentDetails found");
            throw new EntityNotFoundException("PigmyAgentDetails for id not found :" + id);
        }
        PigmyAgent pigmyAgentDetails = pigmyAgentDetailsObj.get();
        return new ResponseEntity<>(pigmyAgentDetails, HttpStatus.OK);
    }

    @GetMapping("/pigmy-agent/by-number/{agentNumber}")
    public ResponseEntity<PigmyAgent> getPigmyAgentDetails(@PathVariable String agentNumber) {
        Optional<PigmyAgent> pigmyAgentDetailsObj = pigmyAgentService.getPigmyAgentByNumber(agentNumber);
        if (!pigmyAgentDetailsObj.isPresent()) {
            log.warn("No PigmyAgentDetails found");
            throw new EntityNotFoundException("PigmyAgentDetails for AgentNumber not found :" + agentNumber);
        }
        PigmyAgent pigmyAgentDetails = pigmyAgentDetailsObj.get();
        return new ResponseEntity<>(pigmyAgentDetails, HttpStatus.OK);
    }


}
