package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.DepositsApproval;

import java.util.List;

public interface DepositsApprovalService {

    DepositsApproval saveDepositsApproval(DepositsApproval depositsApproval);
    List<DepositsApproval> getAllDepositsApprovals();
}
