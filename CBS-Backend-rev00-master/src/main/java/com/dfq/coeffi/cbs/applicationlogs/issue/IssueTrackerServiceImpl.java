package com.dfq.coeffi.cbs.applicationlogs.issue;

import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Service
public class IssueTrackerServiceImpl implements IssueTrackerService {

    private final IssueTrackerRepository issueTrackerRepository;

    @Autowired
    public IssueTrackerServiceImpl(IssueTrackerRepository issueTrackerRepository) {
        this.issueTrackerRepository = issueTrackerRepository;

    }

    @Override
    public IssueTracker createIssueTracker(IssueTracker issueTracker) {
        return issueTrackerRepository.save(issueTracker);
    }

    @Override
    public IssueTracker createIssueTracker(String message, String scenario, User loggedUser) {
        IssueTracker issueTracker = new IssueTracker();
        issueTracker.setErrorStack(message);
        issueTracker.setModuleName("");
        issueTracker.setErrorMessage(scenario);
        issueTracker.setTrackedOn(DateUtil.getTodayDate());
        issueTracker.setTransactionBy(loggedUser.getFirstName());
        return issueTrackerRepository.save(issueTracker);
    }

    @Override
    public List<IssueTracker> listAllIssueTracker() {
        return issueTrackerRepository.findAll();
    }

    @Override
    public Optional<IssueTracker> getIssueTracker(long id) {
        return ofNullable(issueTrackerRepository.findOne(id));
    }

    @Override
    public void deleteIssueTracker(long id) {
        issueTrackerRepository.delete(id);
    }

    @Override
    public List<IssueTracker> getIssueTrackerListByDate(Date date) {
        return issueTrackerRepository.findAll();
    }

    @Override
    public IssueTracker recordIssueTracker(String errorStack, String moduleName, Priority priority) {
        IssueTracker issueTracker = new IssueTracker();
        issueTracker.setErrorStack(errorStack);
        issueTracker.setModuleName(moduleName);
        issueTracker.setTrackedOn(DateUtil.getTodayDate());
        issueTracker.setPriority(priority);
        return issueTrackerRepository.save(issueTracker);
    }
}