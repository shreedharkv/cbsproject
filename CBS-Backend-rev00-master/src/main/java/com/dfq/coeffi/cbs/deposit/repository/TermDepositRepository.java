package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.RecurringDeposit;
import com.dfq.coeffi.cbs.deposit.entity.TermDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@EnableJpaRepositories
public interface TermDepositRepository extends JpaRepository<TermDeposit,Long> {

    @Query("SELECT td FROM TermDeposit td WHERE " +
            "(date(td.createdOn) BETWEEN :dateFrom AND :dateTo and td.status=false)")
    List<TermDeposit> getAllTermDeposits(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT td FROM TermDeposit td WHERE (td.accountNumber=:accountNumber AND td.depositsApproval.isApproved=true AND td.isWithDrawn=false )")
    Optional<TermDeposit> getTermDepositByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT td FROM TermDeposit td WHERE td.member.memberNumber=:memberNumber AND td.depositsApproval.isApproved=true AND td.isWithDrawn=false")
    List<TermDeposit> getTermDepositByMemberNumber(@Param("memberNumber") String memberNumber);
}