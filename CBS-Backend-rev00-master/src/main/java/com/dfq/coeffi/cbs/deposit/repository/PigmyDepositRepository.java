package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import com.dfq.coeffi.cbs.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface PigmyDepositRepository extends JpaRepository<PigmyDeposit,Long> {

    @Query("SELECT pd FROM PigmyDeposit pd WHERE " +
            "(date(pd.createdOn) BETWEEN :dateFrom AND :dateTo and pd.status=false)")
    List<PigmyDeposit> getAllPigmyDeposits(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT pd FROM PigmyDeposit pd WHERE " +
            "(date(pd.createdOn) BETWEEN :dateFrom AND :dateTo and pd.status=:status)")
    List<PigmyDeposit> getAllPigmyDeposits(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,@Param("status") boolean status);

    @Query("SELECT pd FROM PigmyDeposit pd WHERE pd.accountNumber=:accountNumber AND pd.isWithDrawn=false")
    Optional<PigmyDeposit> getPigmyDepositByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT pd FROM PigmyDeposit pd WHERE (pd.member.customer.id=:customerId AND pd.depositsApproval.isApproved=true AND pd.status=false)")
    List<PigmyDeposit> getPigmyDepositByCustomerId(@Param("customerId") long customerId);

    @Query("SELECT pd FROM PigmyDeposit pd WHERE (pd.member=:member AND pd.status=true)")
    List<PigmyDeposit> findByMember(@Param("member") Member member);

    @Query("SELECT pigmyDeposit FROM PigmyDeposit pigmyDeposit WHERE pigmyDeposit.status=true")
    List<PigmyDeposit> getPigmyActiveAcounts();

    @Query("SELECT pigmyDeposit FROM PigmyDeposit pigmyDeposit WHERE pigmyDeposit.member.memberNumber=:memberNumber AND pigmyDeposit.depositsApproval.isApproved=true AND pigmyDeposit.isWithDrawn=false")
    List<PigmyDeposit> getPigmyDepositByMemberNumber(@Param("memberNumber") String memberNumber);

    @Query("SELECT pd FROM PigmyDeposit pd WHERE pd.accountNumber=:accountNumber AND pd.depositsApproval.isApproved=true AND pd.isWithDrawn=false")
    Optional<PigmyDeposit> getPigmyDepositByAccountNumberAndStatus(@Param("accountNumber") String accountNumber);
}