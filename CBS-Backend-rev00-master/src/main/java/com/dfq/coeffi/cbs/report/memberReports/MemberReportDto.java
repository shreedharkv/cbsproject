package com.dfq.coeffi.cbs.report.memberReports;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class MemberReportDto {
    public Date inputDate;
    public Date dateFrom;
    public Date dateTo;
    public String numFrom;
    public String numTo;
    public String reportType;
    public String applicationFrom;
    public String applicationTo;
    public String nominalMemberNumberFrom;
    public String nominalMemberNumberTo;
    public long customerIdFrom;
    public long customerIdTo;
    public String accountNumberFrom;
    public String accountNumberTo;
    public String accountNumber;
    public String memberNumberFrom;
    public String memberNumberTo;
    public String year;
    public String occupationCode;

}
