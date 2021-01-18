package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.DoubleScheme;
import com.dfq.coeffi.cbs.deposit.entity.RecurringDeposit;
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
public interface RecurringDepositRepository extends JpaRepository<RecurringDeposit,Long> {

    @Query("SELECT rd FROM RecurringDeposit rd WHERE " +
            "(date(rd.createdOn) BETWEEN :dateFrom AND :dateTo and rd.status=false)")
    List<RecurringDeposit> getAllRecurringDeposit(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT rd FROM RecurringDeposit rd WHERE " +
            "(date(rd.createdOn) BETWEEN :dateFrom AND :dateTo and rd.status=:status)")
    List<RecurringDeposit> getAllRecurringDeposit(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,@Param("status") boolean status);

    @Query("SELECT rd FROM RecurringDeposit rd WHERE (rd.accountNumber=:accountNumber AND rd.depositsApproval.isApproved=true AND rd.isWithDrawn=false)")
    Optional<RecurringDeposit> getRecurringDepositByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT rd FROM RecurringDeposit rd WHERE (rd.member.customer.id=:customerId AND rd.depositsApproval.isApproved=true AND rd.isWithDrawn=false)")
    List<RecurringDeposit> getRecurringDepositByCustomerId(@Param("customerId") long customerId);

    @Query("SELECT rd FROM RecurringDeposit rd WHERE rd.member.memberNumber=:memberNumber AND rd.depositsApproval.isApproved=true AND rd.isWithDrawn=false")
    List<RecurringDeposit> getRecurringDepositByMemberNumber(@Param("memberNumber") String memberNumber);

    List<RecurringDeposit> findByStatus(boolean status);
}
