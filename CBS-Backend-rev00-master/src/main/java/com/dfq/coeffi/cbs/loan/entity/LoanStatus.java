package com.dfq.coeffi.cbs.loan.entity;

public enum LoanStatus {
    INVALID,
    SUBMITTED_AND_PENDING_APPROVAL,
    APPROVED,
    ACTIVE,
    TRANSFER_IN_PROGRESS,
    TRANSFER_ON_HOLD,
    WITHDRAWN,
    REJECTED,
    CLOSED_OBLIGATIONS_MET,
    CLOSED_WRITTEN_OFF,
    LOAN_CLOSED,
    CLOSED_RESCHEDULE_OUTSTANDING_AMOUNT ,
    OVERPAID,
    SCHEDULED;
}