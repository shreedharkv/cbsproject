package com.dfq.coeffi.cbs.report.depositReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction.CurrentAccountTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;

import java.util.Date;
import java.util.List;

public interface DepositReportService {
    List<FixedDeposit> getFixedDepositReportByDate(Date inputDate, DepositType depositType, String accountNumberFrom, String accountNumberTo, Date dateFrom, Date dateTo, String accountNumber, Date maturityDateFrom, Date maturityDateTo);
    List<CurrentAccount> getCurrentAccountReportByDate(Date inputDate, DepositType depositType, String accountNumberFrom, String accountNumberTo, Date dateFrom, Date dateTo);
    List<ChildrensDeposit> getChildrensDepositReportByDate(Date inputDate, DepositType depositType, String accountNumberFrom, String accountNumberTo, Date dateFrom, Date dateTo, Date maturityDateFrom, Date maturityDateTo);
    List<DoubleScheme> getDoubleSchemeReportByDate(Date inputDate, DepositType depositType, String accountNumberFrom, String accountNumberTo, Date dateFrom, Date dateTo, Date maturityDateFrom, Date maturityDateTo);
    List<RecurringDeposit> getRecurringDepositReportByDate(Date inputDate, DepositType depositType, String accountNumberFrom, String accountNumberTo, Date dateFrom, Date dateTo, Date maturityDateFrom, Date maturityDateTo);
    List<SavingsBankDeposit> getSavingsBankDepositReportByDate(Date inputDate, DepositType depositType);
    List<TermDeposit> getTermDepositReportByDate(Date inputDate, DepositType depositType, String accountNumberFrom, String accountNumberTo, Date dateFrom, Date dateTo, Date maturityDateFrom, Date maturityDateTo);
    List<Loan> getLoanReportByDate(Date inputDate);
    List<Loan> getLoanReportByDate(Date dateFrom, Date dateTo);

    List<CurrentAccount> getCurrentAccountApplicationDetails(Date dateFrom,Date dateTo,String applicationFrom,String applicationTo,String accountNumberFrom,String accountNumberTo);
    List<CurrentAccountTransaction> getCurrentAccountLedgerDetails(Date dateFrom, Date dateTo,String accountNumber);
    List<PigmyDeposit> getPigmyDepositReportByDate(Date inputDate, DepositType depositType, String accountNumberFrom, String accountNumberTo, Date dateFrom, Date dateTo, String accountNumber, Date maturityDateFrom, Date maturityDateTo);

    List<SavingsBankDeposit> getSavingBankDepositLedgerReport(DepositType depositType, String accountNumber);
    List<ChildrensDeposit> getChildrenDepositLedgerReport(DepositType depositType, String accountNumber);
    List<FixedDeposit> getFixedDepositLedgerReport(DepositType depositType, String accountNumber);
    List<CurrentAccount> getCurrentAccountDepositLedgerReport(DepositType depositType, String accountNumber);
    List<DoubleScheme> getDoubleSchemeDepositLedgerReport(DepositType depositType, String accountNumber);
    List<PigmyDeposit> getPigmyDepositLedgerReport(DepositType depositType, String accountNumber);
    List<RecurringDeposit> getRecurringDepositLedgerReport(DepositType depositType, String accountNumber);
    List<TermDeposit> getTermDepositLedgerReport(DepositType depositType, String accountNumber);

}
