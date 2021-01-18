package com.dfq.coeffi.cbs.report.pigmyReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction.PigmyDepositTransaction;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface PigmyReportRepository extends JpaRepository<PigmyDeposit, Long> {

    @Query("SELECT pd FROM PigmyDeposit pd " +
            "WHERE (:dateFrom is null OR :dateTo is null OR date(pd.createdOn) BETWEEN :dateFrom AND :dateTo ) and " +
            "(:applicationFrom is null or :applicationTo is null or pd.applicationNumber BETWEEN :applicationFrom AND :applicationTo)")
    List<PigmyDeposit> getPigmyDepositApplicatinByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
                                                       @Param("applicationFrom") String applicationFrom, @Param("applicationTo") String applicationTo);

    @Query("SELECT pd FROM PigmyDeposit pd " +
            "WHERE (pd.accountNumber BETWEEN :accountNumberFrom AND :accountNumberTo )")
    List<PigmyDeposit> getPigmyDepositMemberByAccountNumber(@Param("accountNumberFrom") String accountNumberFrom, @Param("accountNumberTo") String accountNumberTo);

    @Query("SELECT bm FROM PigmyDepositTransaction bm " +
            "WHERE date(bm.createdOn) BETWEEN '1947-01-01' AND :inputDate and transactionType ='CREDIT')")
    List<PigmyDepositTransaction> getPigmyDepositByDate(@Param("inputDate") Date inputDate);

    @Query("SELECT bm FROM PigmyDepositTransaction bm " +
            "WHERE MONTH(bm.createdOn) =:inputMonth and bm.pigmyDeposit.accountNumber = :accountNumber)")
    List<PigmyDepositTransaction> getPigmyDepositByMonth(@Param("inputMonth") int inputMonth, @Param("accountNumber") String accountNumber);

}
