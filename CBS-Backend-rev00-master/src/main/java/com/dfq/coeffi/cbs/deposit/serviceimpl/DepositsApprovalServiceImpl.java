package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.deposit.entity.DepositsApproval;
import com.dfq.coeffi.cbs.deposit.repository.DepositsApprovalRepository;
import com.dfq.coeffi.cbs.deposit.service.DepositsApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import com.dfq.coeffi.cbs.deposit.service.DepositsApprovalService;

import java.util.List;

@Service
@Transactional
public class DepositsApprovalServiceImpl implements DepositsApprovalService {

    private final DepositsApprovalRepository depositsApprovalRepository;

    @Autowired
    public DepositsApprovalServiceImpl(DepositsApprovalRepository depositsApprovalRepository) {
        this.depositsApprovalRepository = depositsApprovalRepository;
    }

    @Override
    public DepositsApproval saveDepositsApproval(DepositsApproval depositsApproval) {
        return depositsApprovalRepository.save(depositsApproval);
    }

    @Override
    public List<DepositsApproval> getAllDepositsApprovals() {

        return depositsApprovalRepository.findAll();
    }
}
