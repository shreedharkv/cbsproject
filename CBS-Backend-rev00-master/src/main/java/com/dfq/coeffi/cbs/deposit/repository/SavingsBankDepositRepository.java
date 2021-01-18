package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.CurrentAccount;
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

@Transactional
@EnableJpaRepositories
public interface SavingsBankDepositRepository extends JpaRepository<SavingsBankDeposit,Long> {

    @Query("SELECT sb FROM SavingsBankDeposit sb WHERE " +
            "(date(sb.createdOn) BETWEEN :dateFrom AND :dateTo and sb.status=false)")
    List<SavingsBankDeposit> getAllSavingsBankDeposit(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT sb FROM SavingsBankDeposit sb WHERE sb.accountNumber=:accountNumber AND sb.accountStatus ='ACTIVE'")
    SavingsBankDeposit getSavingsBankDepositByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT sb FROM SavingsBankDeposit sb WHERE sb.member.id=:memberId AND sb.status=true AND sb.accountStatus ='ACTIVE'")
    List<SavingsBankDeposit> findByMemberAndStatus(@Param("memberId") long memberId);

    @Query("SELECT sb FROM SavingsBankDeposit sb WHERE sb.member.memberNumber=:memberNumber AND sb.status=true AND sb.accountStatus ='ACTIVE'")
    SavingsBankDeposit getSavingsBankAccountByMemberNumber(@Param("memberNumber") String memberNumber);

    @Query("SELECT sb FROM SavingsBankDeposit sb WHERE sb.member=:member AND sb.status=true AND sb.accountStatus ='ACTIVE'")
    List<SavingsBankDeposit> getSavingBankDepositByMember(@Param("member") Member member);

    @Query("SELECT sb FROM SavingsBankDeposit sb WHERE sb.member=:member AND sb.status=true AND sb.accountStatus ='ACTIVE'")
    SavingsBankDeposit getMemberSBAccount(@Param("member") Member member);

    @Query("SELECT sb FROM SavingsBankDeposit sb WHERE sb.member=:member AND sb.status=true AND sb.accountStatus ='ACTIVE'")
    SavingsBankDeposit getSavingBankDepositByMemberId(@Param("member") Member member);

    @Query("SELECT sb FROM SavingsBankDeposit sb WHERE sb.status=true AND sb.accountStatus ='ACTIVE'")
    List<SavingsBankDeposit> getActiveSavingBankDeposits();
}
