package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.ChildrensDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface ChildrensDepositRepository extends JpaRepository<ChildrensDeposit,Long> {

    @Query("SELECT ca FROM ChildrensDeposit ca WHERE " +
            "(date(ca.createdOn) BETWEEN :dateFrom AND :dateTo and ca.status=false)")
    List<ChildrensDeposit> getAllDeposits(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT d FROM ChildrensDeposit d WHERE d.depositsApproval.isApproved=true")
    List<ChildrensDeposit> getAllApprovedDeposits();

    @Query("SELECT d FROM ChildrensDeposit d WHERE (d.accountNumber=:accountNumber AND d.depositsApproval.isApproved=true AND d.isWithDrawn=false )")
    Optional<ChildrensDeposit> getChildrenDepositByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT d FROM ChildrensDeposit d WHERE (d.member.memberNumber=:memberNumber AND d.depositsApproval.isApproved=true AND d.isWithDrawn=false)")
    List<ChildrensDeposit> getChildrenDepositByMemberNumber(@Param("memberNumber") String memberNumber);
}