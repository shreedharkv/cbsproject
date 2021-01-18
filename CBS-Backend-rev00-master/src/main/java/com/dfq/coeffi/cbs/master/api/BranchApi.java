package com.dfq.coeffi.cbs.master.api;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import com.dfq.coeffi.cbs.master.service.BranchService;
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
public class BranchApi extends BaseController {

    @Autowired
    private BranchService branchService;

    @GetMapping("branch")
    public ResponseEntity<List<Branch>> getActiveBranches() {
        List<Branch> branches = branchService.getBranches();
        if (CollectionUtils.isEmpty(branches)) {
            throw new EntityNotFoundException("No active branch found");
        }
        return new ResponseEntity<>(branches, HttpStatus.OK);
    }


    @GetMapping("active-branch")
    public ResponseEntity<Branch> getActiveBranch() {
        Branch branch = branchService.getCurrentBranch();
        if (branch == null) {
            throw new EntityNotFoundException("No active branch found");
        }
        return new ResponseEntity<>(branch, HttpStatus.OK);
    }

    @PostMapping("branch")
    public ResponseEntity<Branch> createNewBranch(@RequestBody  final Branch branch) {
        Branch persistedObject = branchService.createNewBranch(branch);
        if (persistedObject != null) log.info("New branch created");
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }
}