package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;
import com.dfq.coeffi.cbs.deposit.repository.FixedDepositRepository;
import com.dfq.coeffi.cbs.deposit.service.FixedDepositService;
import com.dfq.coeffi.cbs.loan.entity.LoanStatus;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
@Transactional
public class FixedDepositServiceImpl implements FixedDepositService {

    private final FixedDepositRepository fixedDepositRepository;
    private final LoanService loanService;

    @Autowired
    public FixedDepositServiceImpl(FixedDepositRepository fixedDepositRepository,LoanService loanService) {
        this.fixedDepositRepository = fixedDepositRepository;
        this.loanService = loanService;
    }

    @Override
    public FixedDeposit saveFixedDeposit(FixedDeposit fixedDeposit) {
        return fixedDepositRepository.save(fixedDeposit);
    }

    @Override
    public List<FixedDeposit> getAllFixedDeposits(Date dateFrom,Date dateTo) {
        return fixedDepositRepository.getAllFixedDeposits(dateFrom,dateTo);
    }

    @Override
    public List<FixedDeposit> getAllFixedDeposits(Date dateFrom, Date dateTo, boolean status) {
        return fixedDepositRepository.getAllFixedDeposits(dateFrom,dateTo,status);
    }

    @Override
    public Optional<FixedDeposit> getFixedDepositById(long id) {
        return ofNullable(fixedDepositRepository.findOne(id));
    }

    @Override
    public void deleteFixedDeposit(long id) {
        fixedDepositRepository.delete(id);
    }

    @Override
    public List<FixedDeposit> getFixedDepositListByDate(Date dateFrom,Date dateTo) {
        return fixedDepositRepository.getFixedDepositListByDate(dateFrom,dateTo);
    }

    @Override
    public Optional<FixedDeposit> getFixedDepositByAccountNumber(String accountNumber) {
        return fixedDepositRepository.getFixedDepositByAccountNumber(accountNumber);
    }

    @Override
    public List<FixedDeposit> getFixedDepositByCustomerId(long customerId) {
        return fixedDepositRepository.getFixedDepositByCustomerId(customerId);
    }

    @Override
    public List<FixedDeposit> getFixedDepositByMemberNumber(String memberNumber) {
        return fixedDepositRepository.getFixedDepositByMemberNumber(memberNumber);
    }

    @Override
    public void checkFixedDepositForLoan(String accountNumber) {
        List<Loan> fixedDepositLoans = new ArrayList<>();
        List<Loan> loans = loanService.getFixedDepositLoanByAccountNumber(accountNumber);
        if(loans != null){
            fixedDepositLoans.addAll(loans);
        }
        if(fixedDepositLoans != null && fixedDepositLoans.size() > 0){
            for (Loan loan: fixedDepositLoans) {
                if(!loan.getLoanStatus().equals(LoanStatus.LOAN_CLOSED)){
                    throw new EntityNotFoundException("Fixed Deposit can not be refund !!Active loan found for Acc No:  " +loan.getLoanAccountNumber());
                }
            }
        }
    }

    @Override
    public List<FixedDeposit> getActiveFixedDeposits() {
        return fixedDepositRepository.getActiveFixedDeposits();
    }
}