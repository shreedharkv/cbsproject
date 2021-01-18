package com.dfq.coeffi.cbs.applicationlogs.issue;

import com.dfq.coeffi.cbs.user.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IssueTrackerService {

    IssueTracker createIssueTracker(IssueTracker issueTracker);
    IssueTracker createIssueTracker(String message, String scenario, User loggedUser);
    List<IssueTracker> listAllIssueTracker();
    Optional<IssueTracker> getIssueTracker(long id);
    void deleteIssueTracker(long id);
    List<IssueTracker> getIssueTrackerListByDate(Date date);
    IssueTracker recordIssueTracker(String errorStack, String moduleName, Priority priority);
}