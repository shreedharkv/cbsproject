package com.dfq.coeffi.cbs.deposit.serviceimpl;


import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.deposit.repository.PigmyDepositRepository;
import com.dfq.coeffi.cbs.deposit.service.PigmyDepositService;
import com.dfq.coeffi.cbs.loan.entity.LoanStatus;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.service.LoanService;
import com.dfq.coeffi.cbs.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

@Service
public class PigmyDepositServiceImpl implements PigmyDepositService {

    private final PigmyDepositRepository pigmyDepositRepository;
    private final LoanService loanService;

    @Autowired
    public PigmyDepositServiceImpl(PigmyDepositRepository pigmyDepositRepository, LoanService loanService){
        this.pigmyDepositRepository = pigmyDepositRepository;
        this.loanService = loanService;

    }

    @Override
    public PigmyDeposit createPigmyDeposit(PigmyDeposit pigmyDeposit) {
        return pigmyDepositRepository.save(pigmyDeposit);
    }

    @Override
    public List<PigmyDeposit> getAllPigmyDeposits(Date dateFrom,Date dateTo) {
        return pigmyDepositRepository.getAllPigmyDeposits(dateFrom,dateTo);
    }

    @Override
    public List<PigmyDeposit> getAllPigmyDeposits(Date dateFrom, Date dateTo, boolean status) {
        return pigmyDepositRepository.getAllPigmyDeposits(dateFrom,dateTo,true);
    }

    @Override
    public Optional<PigmyDeposit> getPigmyDepositById(long id) {
        return ofNullable(pigmyDepositRepository.findOne(id));
    }

    @Override
    public Optional<PigmyDeposit> getPigmyDepositByAccountNumber(String accountNumber) {
        return pigmyDepositRepository.getPigmyDepositByAccountNumber(accountNumber);
    }

    @Override
    public List<PigmyDeposit> getPigmyDepositByCustomerId(long customerId) {
        return pigmyDepositRepository.getPigmyDepositByCustomerId(customerId);
    }

    @Override
    public List<PigmyDeposit> getAllPigmyDepositByMember(Member member) {
        return newArrayList(pigmyDepositRepository.findByMember(member));
    }

    @Override
    public List<PigmyDeposit> getPigmyDepositActiveAccounts() {
        return pigmyDepositRepository.getPigmyActiveAcounts();
    }

    @Override
    public List<PigmyDeposit> getPigmyDepositByMemberNumber(String memberNumber) {
        return pigmyDepositRepository.getPigmyDepositByMemberNumber(memberNumber);
    }

    @Override
    public Optional<PigmyDeposit> getPigmyDepositByAccountNumberByStatus(String accountNumber) {
        return pigmyDepositRepository.getPigmyDepositByAccountNumberAndStatus(accountNumber);
    }

    @Override
    public void checkPigmyDepositForLoan(String accountNumber) {
        List<Loan> pigmyDepositLoans = new ArrayList<>();
        List<Loan> loans = loanService.getPigmyDepositLoanByAccountNumber(accountNumber);
        if(loans != null){
            pigmyDepositLoans.addAll(loans);
        }
        if(pigmyDepositLoans != null && pigmyDepositLoans.size() > 0){
            for (Loan loan: pigmyDepositLoans) {
                if(!loan.getLoanStatus().equals(LoanStatus.LOAN_CLOSED)){
                    throw new EntityNotFoundException("Pigmy Deposit can not be refund !!Active loan found for Acc No:  " +loan.getLoanAccountNumber());
                }
            }
        }
    }
}
