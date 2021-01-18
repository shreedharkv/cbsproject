package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;

import java.util.List;

public interface BranchService {

    Branch getCurrentBranch();
    List<Branch> getBranches();
    Branch createNewBranch(Branch branch);
}