package com.dfq.coeffi.cbs.applicationlogs.issue;

import com.dfq.coeffi.cbs.init.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/issue-tracker")
public class IssueTrackerController extends BaseController {

    @Autowired
    private final IssueTrackerService issueTrackerService;

    @Autowired
    public IssueTrackerController(IssueTrackerService issueTrackerService) {
        this.issueTrackerService = issueTrackerService;
    }

    @GetMapping
    public ResponseEntity<List<IssueTracker>> listAllIssueTracker() {
        List<IssueTracker> issueTrackers = issueTrackerService.listAllIssueTracker();
        if (CollectionUtils.isEmpty(issueTrackers)) {
            throw new EntityNotFoundException("issueTrackers");
        }
        return new ResponseEntity<>(issueTrackers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<IssueTracker> createIssueTracker(@Valid @RequestBody IssueTracker issueTracker) {
        IssueTracker persistedIssueTracker = issueTrackerService.createIssueTracker(issueTracker);
        return new ResponseEntity<>(persistedIssueTracker, HttpStatus.ACCEPTED);
    }

    @PutMapping("{id}")
    public ResponseEntity<IssueTracker> update(@PathVariable long id, @Valid @RequestBody IssueTracker issueTracker) {
        Optional<IssueTracker> persistedIssueTracker = issueTrackerService.getIssueTracker(id);
        if (!persistedIssueTracker.isPresent()) {
            throw new EntityNotFoundException(IssueTracker.class.getSimpleName());
        }
        issueTracker.setId(id);
        issueTrackerService.createIssueTracker(issueTracker);
        return new ResponseEntity<>(issueTracker, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<IssueTracker> deleteIssueTracker(@PathVariable long id) {
        Optional<IssueTracker> issueTracker = issueTrackerService.getIssueTracker(id);

        if (!issueTracker.isPresent()) {
            throw new EntityNotFoundException("No issues found");
        }
        issueTrackerService.deleteIssueTracker(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}