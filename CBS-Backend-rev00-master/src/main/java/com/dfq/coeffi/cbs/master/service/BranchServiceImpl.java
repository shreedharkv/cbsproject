package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import com.dfq.coeffi.cbs.master.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Override
    public Branch getCurrentBranch() {
        return branchRepository.findByActive(true);
    }

    @Override
    public List<Branch> getBranches() {
        return branchRepository.findAll();
    }

    @Override
    public Branch createNewBranch(Branch branch) {
        return branchRepository.save(branch);
    }
}