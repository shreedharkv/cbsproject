package com.dfq.coeffi.cbs.report.bankReport;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
public class BankReprotDto {
    public long applicationFrom;
    public long applicationTo;
    public Date dateFrom;
    public Date dateTo;
    public long accountNumberFrom;
    public long accountNumberTo;
    public String reportType;
}
