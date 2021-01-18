package com.dfq.coeffi.cbs.report.dashboardReport;


import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanInstallments;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface DashboardRepository extends JpaRepository<Transaction,Long> {

    @Query("SELECT t from Transaction t WHERE t.transferType ='CASH'")
    List<Transaction> getCashInHand();

    @Query("SELECT fd from FixedDeposit fd WHERE YEARWEEK(fd.maturityDate)=YEARWEEK(NOW())")
    List<FixedDeposit> getNumberOfDepositsRenewal();

    @Query("SELECT fd from ChildrensDeposit fd WHERE YEARWEEK(fd.maturityDate)=YEARWEEK(NOW())")
    List<ChildrensDeposit> getNumberOfChildrensDepositsRenewal();

    @Query("SELECT fd from DoubleScheme fd WHERE YEARWEEK(fd.maturityDate)=YEARWEEK(NOW())")
    List<DoubleScheme> getNumberOfDoubleSchemesRenewal();

    @Query("SELECT fd from RecurringDeposit fd WHERE YEARWEEK(fd.maturityDate)=YEARWEEK(NOW())")
    List<RecurringDeposit> getNumberOfRecurringDepositsRenewal();

    @Query("SELECT fd from TermDeposit fd WHERE YEARWEEK(fd.maturityDate)=YEARWEEK(NOW())")
    List<TermDeposit> getNumberOfTermDepositsRenewal();

    @Query("SELECT li from LoanInstallments li WHERE YEARWEEK(li.dueDate)=YEARWEEK(NOW())")
    List<LoanInstallments> getNumberOfLoanInstallmentsDuesInCurrentWeek();

    @Query("SELECT sb from SavingsBankDeposit sb WHERE sb.accountStatus ='ACTIVE'")
    List<SavingsBankDeposit> getNumberOfSavingBankAccounts();

    @Query("SELECT fd FROM FixedDeposit fd WHERE fd.depositsApproval.isApproved=true")
    List<FixedDeposit> getAllApprovedFixedDeposit();

    @Query("SELECT ds FROM DoubleScheme ds WHERE ds.depositsApproval.isApproved=true")
    List<DoubleScheme> getAllApprovedDoubleScheme();

    @Query("SELECT d FROM ChildrensDeposit d WHERE d.depositsApproval.isApproved=true")
    List<ChildrensDeposit> getAllApprovedDeposits();

    @Query("SELECT td FROM TermDeposit td WHERE td.depositsApproval.isApproved=true")
    List<TermDeposit> getAllApprovedTermDeposit();

    @Query("SELECT rd FROM RecurringDeposit rd WHERE rd.depositsApproval.isApproved=true")
    List<RecurringDeposit> getAllApprovedRecurringDeposit();
}
