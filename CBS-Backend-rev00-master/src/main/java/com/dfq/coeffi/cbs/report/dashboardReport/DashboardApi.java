package com.dfq.coeffi.cbs.report.dashboardReport;

import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanInstallments;
import com.dfq.coeffi.cbs.loan.service.LoanService;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.MemberType;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@RestController
@Slf4j
public class DashboardApi extends BaseController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private MemberService memberService;


    @GetMapping("report/dashboard/cash-in-hand")
    public ResponseEntity<Transaction> getTotalCashAmount() {
        List<Transaction> transactions = dashboardService.getCashInHand();
        if (CollectionUtils.isEmpty(transactions)) {
            log.warn("No active Cash transactions");
            throw new EntityNotFoundException("No active Cash transactions");
        }
        BigDecimal cashReceipt = BigDecimal.ZERO;
        BigDecimal cashPayment = BigDecimal.ZERO;
        BigDecimal cashInHand = BigDecimal.ZERO;
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransferType().equalsIgnoreCase("CASH")) {
                if(transactions.get(i).getTransactionType().equalsIgnoreCase("CREDIT")) {
                    cashReceipt = transactions.get(i).getCreditAmount().add(cashReceipt);
                }else if(transactions.get(i).getTransactionType().equalsIgnoreCase("DEBIT")){
                    cashPayment = transactions.get(i).getDebitAmount().add(cashPayment);
                }
            }
        }
        cashInHand=cashReceipt.subtract(cashPayment);
        Map<String,BigDecimal> pigmyDepositMap = null;
        ArrayList arrayList = null;
        arrayList = new ArrayList();
        pigmyDepositMap = new HashMap<>();
        pigmyDepositMap.put("cashReceipt",cashReceipt);
        pigmyDepositMap.put("cashPayment",cashPayment);
        pigmyDepositMap.put("cashInHand",cashInHand);
        arrayList.add(pigmyDepositMap);
        return new ResponseEntity(arrayList, HttpStatus.OK);
    }

    @GetMapping("report/dashboard/deposit-renewal")
    public ResponseEntity getDepositsRenewal() {
        ArrayList arrayList = new ArrayList();
        List<FixedDeposit> fixedDeposits = dashboardService.getNumberOfDepositsRenewal();
        List<ChildrensDeposit> childrensDeposits= dashboardService.getNumberOfChildrensDepositsRenewal();
        List<DoubleScheme> doubleSchemes= dashboardService.getNumberOfDoubleSchemesRenewal();
        List<RecurringDeposit> recurringDeposits = dashboardService.getNumberOfRecurringDepositsRenewal();
        List<TermDeposit> termDeposits= dashboardService.getNumberOfTermDepositsRenewal();
        long allDeposits = fixedDeposits.size() + childrensDeposits.size() +doubleSchemes.size()+recurringDeposits.size()+termDeposits.size();
        arrayList.add(fixedDeposits);
        arrayList.add(childrensDeposits);
        arrayList.add(doubleSchemes);
        arrayList.add(recurringDeposits);
        arrayList.add(termDeposits);
        if (arrayList.isEmpty()) {
            log.warn("No Deposit-Renewal on this week");
            throw new EntityNotFoundException("No Deposit-Renewal on this week");
        }
        return new ResponseEntity(arrayList, HttpStatus.OK);
    }

    @GetMapping("report/dashboard/loan-duedate")
    public ResponseEntity<List<LoanInstallments>> getLoanDueDate() {
        List<LoanInstallments> loanInstallments = dashboardService.getNumberOfLoanInstallmentsDuesInCurrentWeek();
        if (loanInstallments.isEmpty()) {
            log.warn("No Loan Installments Due on this week");
//            throw new EntityNotFoundException("No Loan Installments Due on this week");
        }
        return new ResponseEntity<>(loanInstallments,HttpStatus.OK);
    }

    @GetMapping("report/dashboard/number-of-saving-bank-accounts")
    public ResponseEntity<List<SavingsBankDeposit>> getSavingBankAccounts() {
        DashboardDto dashboardDto = new DashboardDto();
        List<SavingsBankDeposit> savingsBankDeposits = dashboardService.getNumberOfSavingBankAccounts();
        List<Loan> getAllLAD = loanService.getLoanByLoanType(LoanType.LAD);
        long numberOfLAD = getAllLAD.size();
        if (savingsBankDeposits.isEmpty()) {
            throw new EntityNotFoundException("No Savings Bank Deposits ");
        }
        long allDeposits = getDepositsAll();
        long numberOfAccount =savingsBankDeposits.size();
        dashboardDto.setNumberOfSavingBankAccounts(numberOfAccount);
        dashboardDto.setNumberOfDeposits(allDeposits);
        dashboardDto.setNumberOfLAD(numberOfLAD);
        return new ResponseEntity(dashboardDto,HttpStatus.OK);
    }

    public long getDepositsAll() {
        ArrayList arrayList = new ArrayList();
        List<FixedDeposit> fixedDeposits = dashboardService.getNumberOfDepositsRenewal();
        List<ChildrensDeposit> childrensDeposits= dashboardService.getNumberOfChildrensDepositsRenewal();
        List<DoubleScheme> doubleSchemes= dashboardService.getNumberOfDoubleSchemesRenewal();
        List<RecurringDeposit> recurringDeposits = dashboardService.getNumberOfRecurringDepositsRenewal();
        List<TermDeposit> termDeposits= dashboardService.getNumberOfTermDepositsRenewal();
        long allDeposits = fixedDeposits.size() + childrensDeposits.size() +doubleSchemes.size()+recurringDeposits.size()+termDeposits.size();
        arrayList.add(fixedDeposits);
        arrayList.add(childrensDeposits);
        arrayList.add(doubleSchemes);
        arrayList.add(recurringDeposits);
        arrayList.add(termDeposits);
        if (arrayList.isEmpty()) {
            log.warn("No Deposit-Renewal on this week");
            throw new EntityNotFoundException("No Deposit-Renewal on this week");
        }
        return allDeposits;
    }

    @GetMapping("report/dashboard/all-deposits")
    public ResponseEntity<List<ChildrensDeposit>> getApprovedDeposits() {
        Map<String, Long> allDeposits = null;
        List<ChildrensDeposit> childrensDeposits = dashboardService.getAllApprovedDeposits();
        List<DoubleScheme> doubleSchemes= dashboardService.getAllApprovedDoubleScheme();
        List<FixedDeposit> fixedDeposits= dashboardService.getAllApprovedFixedDeposit();
        List<TermDeposit> termDeposits= dashboardService.getAllApprovedTermDeposit();
        List<RecurringDeposit> recurringDeposits= dashboardService.getAllApprovedRecurringDeposit();

        allDeposits = new HashMap();
        allDeposits.put("CD", Long.valueOf(childrensDeposits.size()));
        allDeposits.put("DS", Long.valueOf(doubleSchemes.size()));
        allDeposits.put("FD", Long.valueOf(fixedDeposits.size()));
        allDeposits.put("TD", Long.valueOf(termDeposits.size()));
        allDeposits.put("RD", Long.valueOf(recurringDeposits.size()));
        return new ResponseEntity(allDeposits,HttpStatus.OK);
    }

    @GetMapping("report/dashboard/member-nominal")
    public ResponseEntity<List<Member>> getApprovedMemberAndNominal() {
        Map<String, Long> members = null;
        List<Member> member= memberService.getApprovedMembers(MemberType.MEMBER);
        List<Member> nominal= memberService.getApprovedMembers(MemberType.NOMINAL);
        members = new HashMap();
        members.put("MEMBER", Long.valueOf(member.size()));
        members.put("NOMINAL", Long.valueOf(nominal.size()));
        return new ResponseEntity(members,HttpStatus.OK);
    }
}
