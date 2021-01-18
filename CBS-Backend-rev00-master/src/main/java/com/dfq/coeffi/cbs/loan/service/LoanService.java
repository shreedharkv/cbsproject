package com.dfq.coeffi.cbs.loan.service;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.loan.entity.LoanStatus;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanInstallments;
import com.dfq.coeffi.cbs.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    Loan applyLean(Loan loan);
    List<Loan> appliedGoldLoans();
    List<Loan> appliedGoldLoans(boolean status);

    List<Loan> getLoanByLoanType(LoanType loanType);
    Optional<Loan> appliedGoldLoan(long loanId);
    Optional<Loan> findGoldLoanByApplicationNo(long applicationNo);
    Optional<Loan> findLoanByAccountNo(String accountNumber);
    Loan getLatestLoanApplication();
    List<Loan> findLoanByMember(Member member);
    List<Loan> findLoanByCustomer(Customer customer, LoanStatus loanStatus);
    List<Loan> getFixedDepositLoans(Customer customer, LoanStatus loanStatus);
    List<Loan> getMemberTermLoans(Customer customer, LoanStatus loanStatus);

    List<Loan> findLoanByCustomer(Customer customer);
    List<Loan> getFixedDepositLoans(Customer customer);
    List<Loan> getMemberTermLoans(Customer customer);

    List<Loan> getFixedDepositLoanByAccountNumber(String accountNumber);
    List<Loan> getRecurringDepositLoanById(String accountNumber);
    List<Loan> getPigmyDepositLoanByAccountNumber(String accountNumber);

    LoanInstallments updateLoanInstallment(LoanInstallments loanInstallments);
}