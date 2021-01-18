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

@EnableJpaRepositories
@Transactional
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount,Long> {

    @Query("SELECT ca FROM CurrentAccount ca WHERE " +
            "(date(ca.createdOn) BETWEEN :dateFrom AND :dateTo and ca.status=false)")
    List<CurrentAccount> getAllCurrentAccountDeposits(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    List<CurrentAccount> findByMember(Member member);

    @Query("SELECT currentAccount FROM CurrentAccount currentAccount WHERE currentAccount.accountNumber=:accountNumber AND currentAccount.accountStatus = 'ACTIVE' ")
    Optional<CurrentAccount> getCurrentAccountDepositByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT ca FROM CurrentAccount ca WHERE ca.member=:member AND ca.status=true AND ca.accountStatus = 'ACTIVE' ")
    List<CurrentAccount> getCurrentAccountByMember(@Param("member") Member member);
}
