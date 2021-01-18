package com.dfq.coeffi.cbs.report.savingBankReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import java.util.Date;
import java.util.List;

public interface SavingBankReportService {
    List<SavingsBankDeposit>  getSavingBankApplicationDetails(Date dateFrom, Date dateTo, String applicationNumberFrom, String applicationNumberTo);
    List<SavingsBankDeposit>  getSavingBankMemberDetails(String accountNumberFrom, String accountNumberTo);
    List<SavingBankTransaction>  getSBLedgerDetails(String accountNumber, Date dateFrom, Date dateTo);
}
