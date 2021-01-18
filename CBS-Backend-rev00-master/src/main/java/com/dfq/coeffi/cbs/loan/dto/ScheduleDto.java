package com.dfq.coeffi.cbs.loan.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Setter
@Getter
public class ScheduleDto {

    private String loanAccountNumber;
    private  int installments;

    @Temporal(TemporalType.DATE)
    private Date repaymentDate;
}
