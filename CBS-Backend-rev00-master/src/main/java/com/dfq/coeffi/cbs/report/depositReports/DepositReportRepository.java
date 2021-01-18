package com.dfq.coeffi.cbs.report.depositReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface DepositReportRepository extends JpaRepository<FixedDeposit,Long> {

    @Query("SELECT fd FROM FixedDeposit fd WHERE (date(fd.createdOn) BETWEEN '1947-01-01' AND :inputDate and fd.status=true and fd.depositType =:depositType) OR " +
            "(fd.accountNumber BETWEEN :accountNumberFrom AND :accountNumberTo) OR " +
            "((date(fd.createdOn) BETWEEN :dateFrom AND :dateTo) AND " +
            "(fd.accountNumber =:accountNumber)) OR " +
            "(date(fd.maturityDate) BETWEEN :maturityDateFrom AND :maturityDateTo) OR" +
            "(date(fd.createdOn) BETWEEN :dateFrom AND :dateTo)")
    List<FixedDeposit> getFixedDepositReportByDate(@Param("inputDate") Date inputDate, @Param("depositType") DepositType depositType,
                                                   @Param("accountNumberFrom") String accountNumberFrom, @Param("accountNumberTo") String accountNumberTo,
                                                   @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("accountNumber") String accountNumber,
                                                   @Param("maturityDateFrom") Date maturityDateFrom, @Param("maturityDateTo") Date maturityDateTo);

    @Query("SELECT ca FROM CurrentAccount ca WHERE (date(ca.createdOn) BETWEEN '1947-01-01' AND :inputDate and ca.status=true and ca.depositType =:depositType) OR " +
            "(ca.accountNumber BETWEEN :accountNumberFrom AND :accountNumberTo) OR " +
            "(date(ca.createdOn) BETWEEN :dateFrom AND :dateTo)")
    List<CurrentAccount> getCurrentAccountReportByDate(@Param("inputDate") Date inputDate, @Param("depositType") DepositType depositType,
                                                       @Param("accountNumberFrom") String accountNumberFrom, @Param("accountNumberTo") String accountNumberTo,
                                                       @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT cd FROM ChildrensDeposit cd WHERE (date(cd.createdOn) BETWEEN '1947-01-01' AND :inputDate and cd.status=true and cd.depositType =:depositType) OR " +
            "(cd.accountNumber BETWEEN :accountNumberFrom AND :accountNumberTo) OR " +
            "(date(cd.createdOn) BETWEEN :dateFrom AND :dateTo) OR " +
            "(date(cd.maturityDate) BETWEEN :maturityDateFrom AND :maturityDateTo)")
    List<ChildrensDeposit> getChildrensDepositReportByDate(@Param("inputDate") Date inputDate, @Param("depositType") DepositType depositType,
                                                           @Param("accountNumberFrom") String accountNumberFrom, @Param("accountNumberTo") String accountNumberTo,
                                                           @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
                                                           @Param("maturityDateFrom") Date maturityDateFrom, @Param("maturityDateTo") Date maturityDateTo);

    @Query("SELECT ds FROM DoubleScheme ds WHERE (date(ds.createdOn) BETWEEN '1947-01-01' AND :inputDate and ds.status=true and ds.depositType =:depositType) OR " +
            "(ds.accountNumber BETWEEN :accountNumberFrom AND :accountNumberTo) OR " +
            "(date(ds.createdOn) BETWEEN :dateFrom AND :dateTo) OR " +
            "(date(ds.maturityDate) BETWEEN :maturityDateFrom AND :maturityDateTo)")
    List<DoubleScheme> getDoubleSchemeReportByDate(@Param("inputDate") Date inputDate, @Param("depositType") DepositType depositType,
                                                   @Param("accountNumberFrom") String accountNumberFrom, @Param("accountNumberTo") String accountNumberTo,
                                                   @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
                                                   @Param("maturityDateFrom") Date maturityDateFrom, @Param("maturityDateTo") Date maturityDateTo);

    @Query("SELECT rd FROM RecurringDeposit rd WHERE (date(rd.createdOn) BETWEEN '1947-01-01' AND :inputDate and rd.status=true and rd.depositType =:depositType) OR " +
            "(rd.accountNumber BETWEEN :accountNumberFrom AND :accountNumberTo) OR " +
            "(date(rd.createdOn) BETWEEN :dateFrom AND :dateTo) OR" +
            "(date(rd.maturityDate) BETWEEN :maturityDateFrom AND :maturityDateTo)")
    List<RecurringDeposit> getRecurringDepositReportByDate(@Param("inputDate") Date inputDate, @Param("depositType") DepositType depositType,
                                                           @Param("accountNumberFrom") String accountNumberFrom, @Param("accountNumberTo") String accountNumberTo,
                                                           @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
                                                           @Param("maturityDateFrom") Date maturityDateFrom, @Param("maturityDateTo") Date maturityDateTo);

    @Query("SELECT sb FROM SavingsBankDeposit sb WHERE date(sb.createdOn) BETWEEN '1947-01-01' AND :inputDate and sb.status=true and sb.depositType  =:depositType")
    List<SavingsBankDeposit> getSavingsBankDepositReportByDate(@Param("inputDate") Date inputDate, @Param("depositType") DepositType depositType);

    @Query("SELECT td FROM TermDeposit td WHERE (date(td.createdOn) BETWEEN '1947-01-01' AND :inputDate and td.status=true and td.depositType =:depositType) OR " +
            "(td.accountNumber BETWEEN :accountNumberFrom AND :accountNumberTo) OR " +
            "(date(td.createdOn) BETWEEN :dateFrom AND :dateTo) OR " +
            "(date(td.maturityDate) BETWEEN :maturityDateFrom AND :maturityDateTo)")
    List<TermDeposit> getTermDepositReportByDate(@Param("inputDate") Date inputDate, @Param("depositType") DepositType depositType,
                                                 @Param("accountNumberFrom") String accountNumberFrom, @Param("accountNumberTo") String accountNumberTo,
                                                 @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
                                                 @Param("maturityDateFrom") Date maturityDateFrom, @Param("maturityDateTo") Date maturityDateTo);

    @Query("SELECT l FROM Loan l WHERE date(l.loanDetail.approvedOn) BETWEEN '1947-01-01' AND :inputDate and l.active=true")
    List<Loan> getLoanReportByDate(@Param("inputDate") Date inputDate);

    @Query("SELECT l FROM Loan l WHERE date(l.loanDetail.approvedOn) BETWEEN :dateFrom AND :dateTo and l.active=true")
    List<Loan> getLoanReportByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT ca FROM CurrentAccount ca " +
            "WHERE (date(ca.createdOn) BETWEEN :dateFrom AND :dateTo ) OR " +
            "(ca.applicationNumber BETWEEN :applicationFrom AND :applicationTo ) OR " +
            "(ca.accountNumber BETWEEN :accountNumberFrom AND :accountNumberTo )")
    List<CurrentAccount> getCurrentAccountApplicationDetails(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("applicationFrom") String applicationFrom, @Param("applicationTo") String applicationTo,
                                                             @Param("accountNumberFrom") String accountNumberFrom, @Param("accountNumberTo") String accountNumberTo);

    @Query("SELECT ca FROM CurrentAccountTransaction ca " +
            "WHERE (date(ca.createdOn) BETWEEN :dateFrom AND :dateTo ) and " +
            "(ca.currentAccount.accountNumber =:accountNumber)")
    List<CurrentAccountTransaction> getCurrentAccountLedgerDetails(@Param("dateFrom") Date dateFrom,
                                                                   @Param("dateTo") Date dateTo, @Param("accountNumber") String accountNumber);

    @Query("SELECT fd FROM PigmyDeposit fd WHERE (date(fd.createdOn) BETWEEN '1947-01-01' AND :inputDate and fd.status=true and fd.depositType =:depositType) OR " +
            "(fd.accountNumber BETWEEN :accountNumberFrom AND :accountNumberTo) OR " +
            "((date(fd.createdOn) BETWEEN :dateFrom AND :dateTo) AND " +
            "(fd.accountNumber =:accountNumber)) OR " +
            "(date(fd.maturityDate) BETWEEN :maturityDateFrom AND :maturityDateTo) OR" +
            "(date(fd.createdOn) BETWEEN :dateFrom AND :dateTo)")
    List<PigmyDeposit> getPigmyDepositReportByDate(@Param("inputDate") Date inputDate, @Param("depositType") DepositType depositType,
                                                   @Param("accountNumberFrom") String accountNumberFrom, @Param("accountNumberTo") String accountNumberTo,
                                                   @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("accountNumber") String accountNumber,
                                                   @Param("maturityDateFrom") Date maturityDateFrom, @Param("maturityDateTo") Date maturityDateTo);



    @Query("SELECT ca FROM SavingsBankDeposit ca WHERE ca.depositType =:depositType AND ca.accountNumber=:accountNumber")
    List<SavingsBankDeposit> getSavingBankDepositLedgerReport(@Param("depositType") DepositType depositType,
                                                       @Param("accountNumber") String accountNumber);

    @Query("SELECT ca FROM ChildrensDeposit ca WHERE ca.depositType =:depositType AND ca.accountNumber=:accountNumber")
    List<ChildrensDeposit> getChildrenDepositLedgerReport(@Param("depositType") DepositType depositType,
                                                              @Param("accountNumber") String accountNumber);

    @Query("SELECT ca FROM FixedDeposit ca WHERE ca.depositType =:depositType AND ca.accountNumber=:accountNumber")
    List<FixedDeposit> getFixedDepositLedgerReport(@Param("depositType") DepositType depositType,
                                                            @Param("accountNumber") String accountNumber);

    @Query("SELECT ca FROM CurrentAccount ca WHERE ca.depositType =:depositType AND ca.accountNumber=:accountNumber")
    List<CurrentAccount> getCurrentAccountDepositLedgerReport(@Param("depositType") DepositType depositType,
                                                            @Param("accountNumber") String accountNumber);

    @Query("SELECT ca FROM DoubleScheme ca WHERE ca.depositType =:depositType AND ca.accountNumber=:accountNumber")
    List<DoubleScheme> getDoubleSchemeDepositLedgerReport(@Param("depositType") DepositType depositType,
                                                            @Param("accountNumber") String accountNumber);

    @Query("SELECT ca FROM PigmyDeposit ca WHERE ca.depositType =:depositType AND ca.accountNumber=:accountNumber")
    List<PigmyDeposit> getPigmyDepositLedgerReport(@Param("depositType") DepositType depositType,
                                                            @Param("accountNumber") String accountNumber);

    @Query("SELECT ca FROM RecurringDeposit ca WHERE ca.depositType =:depositType AND ca.accountNumber=:accountNumber")
    List<RecurringDeposit> getRecurringDepositLedgerReport(@Param("depositType") DepositType depositType,
                                                            @Param("accountNumber") String accountNumber);

    @Query("SELECT ca FROM TermDeposit ca WHERE ca.depositType =:depositType AND ca.accountNumber=:accountNumber")
    List<TermDeposit> getTermDepositLedgerReport(@Param("depositType") DepositType depositType,
                                                            @Param("accountNumber") String accountNumber);

}