package com.dfq.coeffi.cbs.applicationlogs.issue;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity(name = "application_logged_issues")
public class IssueTracker implements Serializable {

    private static final long serialVersionUID = -8044937119113724010L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date trackedOn;

    private String moduleName;

    @Column(length = 10000)
    private String errorStack;

    @Column(length = 10000)
    private String errorMessage;

    @Column
    private String transactionBy;

    private boolean status;

    @Enumerated(EnumType.STRING)
    private Priority priority;
}