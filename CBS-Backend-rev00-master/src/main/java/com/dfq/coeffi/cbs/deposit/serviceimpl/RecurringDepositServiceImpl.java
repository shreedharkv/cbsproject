package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.deposit.entity.RecurringDeposit;
import com.dfq.coeffi.cbs.deposit.repository.RecurringDepositRepository;
import com.dfq.coeffi.cbs.deposit.service.RecurringDepositService;
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
public class RecurringDepositServiceImpl implements RecurringDepositService {

    private final RecurringDepositRepository recurringDepositRepository;
    private final LoanService loanService;

    @Autowired
    public RecurringDepositServiceImpl(RecurringDepositRepository recurringDepositRepository,LoanService loanService) {
        this.recurringDepositRepository = recurringDepositRepository;
        this.loanService = loanService;
    }

    @Override
    public RecurringDeposit saveRecurringDeposit(RecurringDeposit recurringDeposit) {
        return recurringDepositRepository.save(recurringDeposit);
    }


    @Override
    public List<RecurringDeposit> getAllRecurringDeposit(Date dateFrom,Date dateTo) {
        return recurringDepositRepository.getAllRecurringDeposit(dateFrom,dateTo);
    }

    @Override
    public List<RecurringDeposit> getAllRecurringDeposit(Date dateFrom, Date dateTo, boolean status) {
        return recurringDepositRepository.getAllRecurringDeposit(dateFrom,dateTo,status);
    }

    @Override
    public Optional<RecurringDeposit> getRecurringDepositById(long id) {
        return ofNullable(recurringDepositRepository.findOne(id));
    }

    @Override
    public void deleteRecurringDeposit(long id) {
        recurringDepositRepository.delete(id);
    }

    @Override
    public Optional<RecurringDeposit> getRecurringDepositByAccountNumber(String accountNumber) {
        return recurringDepositRepository.getRecurringDepositByAccountNumber(accountNumber);
    }

    @Override
    public List<RecurringDeposit> getRecurringDepositByCustomerId(long customerId) {
        return recurringDepositRepository.getRecurringDepositByCustomerId(customerId);
    }

    @Override
    public List<RecurringDeposit> getRecuringDepositByMemberNumber(String memberNumber) {
        return recurringDepositRepository.getRecurringDepositByMemberNumber(memberNumber);
    }

    @Override
    public void checkRecurringDepositForLoan(String accountNumber) {
        List<Loan> recurringDepositLoans = new ArrayList<>();
        List<Loan> loans = loanService.getRecurringDepositLoanById(accountNumber);
        if(loans != null){
            recurringDepositLoans.addAll(loans);
        }
        if(recurringDepositLoans != null && recurringDepositLoans.size() > 0){
            for (Loan loan: recurringDepositLoans) {
                if(!loan.getLoanStatus().equals(LoanStatus.LOAN_CLOSED)){
                    throw new EntityNotFoundException("Recurring Deposit can not be refund !!Active loan found for Acc No:  " +loan.getLoanAccountNumber());
                }
            }
        }
    }

    @Override
    public List<RecurringDeposit> getAllRecurringDeposit() {
        return recurringDepositRepository.findByStatus(true);
    }
}
