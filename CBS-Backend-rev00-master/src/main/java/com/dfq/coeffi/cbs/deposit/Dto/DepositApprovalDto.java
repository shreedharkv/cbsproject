package com.dfq.coeffi.cbs.deposit.Dto;

import com.dfq.coeffi.cbs.deposit.entity.DepositsApproval;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class DepositApprovalDto {

    public List<Long> ids;
    public boolean isApproved;
    public long applicationNumber;
    public DepositsApproval depositApproval;
    public String remarks;
    public Date dateFrom;
    public Date dateTo;

    public String accountNumber;

}
