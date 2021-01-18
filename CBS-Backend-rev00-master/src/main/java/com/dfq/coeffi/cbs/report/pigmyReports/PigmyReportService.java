package com.dfq.coeffi.cbs.report.pigmyReports;

import com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction.PigmyDepositTransaction;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import java.util.Date;
import java.util.List;

public interface PigmyReportService {
    List<PigmyDeposit> getPigmyDepositApplicatinByDate(Date dateFrom, Date dateTo, String applicationFrom, String applicationTo);

    List<PigmyDeposit> getPigmyDepositMemberByAccountNumber(String accountNumberFrom, String accountNumberTo);

    List<PigmyDepositTransaction> getPigmyDepositByDate(Date inputDate);

    List<PigmyDepositTransaction> getPigmyDepositByMonth(int inputMonth, String accountNumber);

}
