package com.dfq.coeffi.cbs.member.repository;

import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import com.dfq.coeffi.cbs.member.entity.DividendIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DividendIssueRepository extends JpaRepository<DividendIssue, Long> {

    @Query("select d from DividendIssue d where d.financialYear= :financialYear")
    List<DividendIssue> getCurrentYearDividendIssues(@Param("financialYear") FinancialYear financialYear);


    List<DividendIssue> findByDividendYear(String dividendYear);
}