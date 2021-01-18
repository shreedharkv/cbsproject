package com.dfq.coeffi.cbs.loan.service;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.loan.entity.LoanStatus;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanInstallments;
import com.dfq.coeffi.cbs.loan.repository.GoldLoanRepository;
import com.dfq.coeffi.cbs.loan.repository.LoanInstallmentRepository;
import com.dfq.coeffi.cbs.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private GoldLoanRepository goldLoanRepository;

    @Autowired
    private LoanInstallmentRepository installmentRepository;

    @Override
    public Loan applyLean(Loan loan) {
        return goldLoanRepository.save(loan);
    }


    @Override
    public List<Loan> appliedGoldLoans() {
        return goldLoanRepository.findByActive(true);
    }


    @Override
    public List<Loan> appliedGoldLoans(boolean status) {
        return goldLoanRepository.getUnApprovedLoans(status);
    }

    @Override
    public List<Loan> getLoanByLoanType(LoanType loanType) {
        return goldLoanRepository.findByLoanTypeAndActive(loanType, true);
    }

    @Override
    public Optional<Loan> appliedGoldLoan(long loanId) {
        return ofNullable(goldLoanRepository.findOne(loanId));
    }

    @Override
    public Optional<Loan> findGoldLoanByApplicationNo(long applicationNo) {
        return ofNullable(goldLoanRepository.findByApplicationNumber(applicationNo));
    }

    @Override
    public Optional<Loan> findLoanByAccountNo(String accountNumber) {
        System.out.println(accountNumber);
        return ofNullable(goldLoanRepository.findByLoanAccountNumber(accountNumber));
    }

    @Override
    public Loan getLatestLoanApplication() {
        return goldLoanRepository.findFirstByOrderByIdDesc();
    }

    @Override
    public List<Loan> findLoanByMember(Member member) {
        return goldLoanRepository.findByMember(member);
    }

    @Override
    public List<Loan> findLoanByCustomer(Customer customer, LoanStatus loanStatus) {
        return goldLoanRepository.findLoanByCustomerId(customer.getId(),loanStatus);
    }

    @Override
    public List<Loan> getFixedDepositLoans(Customer customer, LoanStatus loanStatus) {
        return goldLoanRepository.getFixedDepositLoans(customer.getId(),loanStatus);
    }

    @Override
    public List<Loan> getMemberTermLoans(Customer customer, LoanStatus loanStatus) {
        return goldLoanRepository.getMemberTermLoans(customer.getId(),loanStatus);
    }

    @Override
    public List<Loan> findLoanByCustomer(Customer customer) {
        return goldLoanRepository.findLoanByCustomerId(customer.getId());
    }

    @Override
    public List<Loan> getFixedDepositLoans(Customer customer) {
        return goldLoanRepository.getFixedDepositLoans(customer.getId());
    }

    @Override
    public List<Loan> getMemberTermLoans(Customer customer) {
        return goldLoanRepository.getMemberTermLoans(customer.getId());
    }

    @Override
    public List<Loan> getFixedDepositLoanByAccountNumber(String accountNumber) {
        return goldLoanRepository.goldLoanRepository(accountNumber, LoanStatus.WITHDRAWN);
    }

    @Override
    public List<Loan> getRecurringDepositLoanById(String accountNumber) {
        return goldLoanRepository.getLoanAgainstRecuringDeposit(accountNumber, LoanStatus.WITHDRAWN);
    }

    @Override
    public List<Loan> getPigmyDepositLoanByAccountNumber(String accountNumber) {
        return goldLoanRepository.getLoanAgainstPigmyDeposit(accountNumber, LoanStatus.WITHDRAWN);
    }

    @Override
    public LoanInstallments updateLoanInstallment(LoanInstallments loanInstallments) {
        return installmentRepository.save(loanInstallments);
    }


}