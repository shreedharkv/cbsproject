package com.dfq.coeffi.cbs.exception;

import com.dfq.coeffi.cbs.applicationlogs.issue.IssueTrackerService;
import com.dfq.coeffi.cbs.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LoanNotScheduleException extends RuntimeException {


    public LoanNotScheduleException(String message) {
        super(message);
    }

    public LoanNotScheduleException(String message, String scenario, IssueTrackerService issueTrackerService, User loggedUser) {
        super(message);
        issueTrackerService.createIssueTracker(message, scenario, loggedUser);
    }

    public LoanNotScheduleException(String message, Throwable cause) {
        super(message, cause);
        System.out.println("EXCEPTION MESSAGE 2" + message);

    }
}