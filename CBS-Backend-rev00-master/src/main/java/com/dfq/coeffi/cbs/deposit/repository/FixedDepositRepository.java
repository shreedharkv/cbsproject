package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
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
public interface FixedDepositRepository extends JpaRepository<FixedDeposit,Long> {
    @Query("SELECT fd FROM FixedDeposit fd WHERE fd.status=false AND date(fd.createdOn) BETWEEN :dateFrom and :dateTo")
    List<FixedDeposit> getFixedDepositListByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT fd FROM FixedDeposit fd WHERE fd.status=false")
    List<FixedDeposit> getAllFixedDeposits();

    @Query("SELECT fd FROM FixedDeposit fd WHERE (fd.accountNumber=:accountNumber AND fd.depositsApproval.isApproved=true AND fd.isWithDrawn=false )")
    Optional<FixedDeposit> getFixedDepositByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT fd FROM FixedDeposit fd WHERE fd.status=false AND date(fd.createdOn) BETWEEN :dateFrom and :dateTo")
    List<FixedDeposit> getAllFixedDeposits(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT fd FROM FixedDeposit fd WHERE fd.status=:status AND date(fd.createdOn) BETWEEN :dateFrom and :dateTo")
    List<FixedDeposit> getAllFixedDeposits(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,@Param("status")boolean status);

    @Query("SELECT fd FROM FixedDeposit fd WHERE (fd.member.customer.id=:customerId AND fd.depositsApproval.isApproved=true AND fd.isWithDrawn=false )")
    List<FixedDeposit> getFixedDepositByCustomerId(@Param("customerId") long customerId);

    @Query("SELECT fd FROM FixedDeposit fd WHERE fd.member.memberNumber=:memberNumber AND fd.depositsApproval.isApproved=true AND fd.isWithDrawn=false")
    List<FixedDeposit> getFixedDepositByMemberNumber(@Param("memberNumber") String memberNumber);

    @Query("SELECT fd FROM FixedDeposit fd WHERE fd.depositsApproval.isApproved=true AND fd.isWithDrawn=false")
    List<FixedDeposit> getActiveFixedDeposits();
}