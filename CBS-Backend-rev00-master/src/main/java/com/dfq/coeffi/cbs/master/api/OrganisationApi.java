package com.dfq.coeffi.cbs.master.api;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import com.dfq.coeffi.cbs.master.entity.occupation.Occupation;
import com.dfq.coeffi.cbs.master.entity.organisation.Organisation;
import com.dfq.coeffi.cbs.master.service.BranchService;
import com.dfq.coeffi.cbs.master.service.OrganisationService;
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
public class OrganisationApi extends BaseController {

    @Autowired
    private OrganisationService organisationService;

    @GetMapping("organisation")
    public ResponseEntity<List<Organisation>> getOrganisations() {
        List<Organisation> organisations = organisationService.getOrganisations();
        if (CollectionUtils.isEmpty(organisations)) {
            throw new EntityNotFoundException("No active organisations found");
        }
        return new ResponseEntity<>(organisations, HttpStatus.OK);
    }

    @GetMapping("occupation")
    public ResponseEntity<List<Occupation>> getOccupations() {
        List<Occupation> occupations = organisationService.getOccupations();
        if (CollectionUtils.isEmpty(occupations)) {
            throw new EntityNotFoundException("No active branch found");
        }
        return new ResponseEntity<>(occupations, HttpStatus.OK);
    }
}