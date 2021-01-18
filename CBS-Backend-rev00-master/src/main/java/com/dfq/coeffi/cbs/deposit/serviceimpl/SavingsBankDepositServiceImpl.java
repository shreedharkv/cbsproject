package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.admin.entity.BODDate;
import com.dfq.coeffi.cbs.deposit.Dto.DepositDto;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.deposit.repository.SavingsBankDepositRepository;
import com.dfq.coeffi.cbs.deposit.service.*;
import com.dfq.coeffi.cbs.loan.entity.LoanStatus;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.service.LoanService;
import com.dfq.coeffi.cbs.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

@Service
@Transactional
public class SavingsBankDepositServiceImpl implements SavingsBankDepositService {

    private final SavingsBankDepositRepository savingsBankDepositRepository;
    private final LoanService loanService;
    private final ChildrensDepositService childrensDepositService;
    private final DoubleSchemeService doubleSchemeService;
    private final FixedDepositService fixedDepositService;
    private final TermDepositService termDepositService;
    private final RecurringDepositService recurringDepositService;

    @Autowired
    public SavingsBankDepositServiceImpl(SavingsBankDepositRepository savingsBankDepositRepository, LoanService loanService,
                                         ChildrensDepositService childrensDepositService, DoubleSchemeService doubleSchemeService,
                                         FixedDepositService fixedDepositService, TermDepositService termDepositService,
                                         RecurringDepositService recurringDepositService) {
        this.savingsBankDepositRepository = savingsBankDepositRepository;
        this.loanService = loanService;
        this.childrensDepositService = childrensDepositService;
        this.doubleSchemeService = doubleSchemeService;
        this.fixedDepositService = fixedDepositService;
        this.termDepositService = termDepositService;
        this.recurringDepositService = recurringDepositService;
    }

    @Override
    public SavingsBankDeposit saveSavingsBankDeposit(SavingsBankDeposit savingsBankDeposit) {
        return savingsBankDepositRepository.save(savingsBankDeposit);
    }

    @Override
    public List<SavingsBankDeposit> getAllSavingsBankDeposit(Date dateFrom, Date dateTo) {
        return savingsBankDepositRepository.getAllSavingsBankDeposit(dateFrom, dateTo);
    }

    @Override
    public Optional<SavingsBankDeposit> getSavingsBankDepositById(long id) {
        return ofNullable(savingsBankDepositRepository.findOne(id));
    }

    @Override
    public void deleteSavingsBankDeposit(long id) {
        savingsBankDepositRepository.delete(id);
    }

    @Override
    public SavingsBankDeposit getSavingsBankDepositByAccountNumber(String accountNumber) {
        return savingsBankDepositRepository.getSavingsBankDepositByAccountNumber(accountNumber);
    }

    @Override
    public SavingsBankDeposit getSavingsBankAccountsByMember(Member member) {
        return savingsBankDepositRepository.getMemberSBAccount(member);
    }

    @Override
    public List<SavingsBankDeposit> getAllSavingsBankAccountsByMember(Member member) {
        return newArrayList(savingsBankDepositRepository.getSavingBankDepositByMember(member));
    }

    @Override
    public void checkSavingBankAccount(Member member) {
        List<SavingsBankDeposit> savingsBankDeposit = savingsBankDepositRepository.findByMemberAndStatus(member.getId());
        if (savingsBankDeposit == null || savingsBankDeposit.size() == 0) {
            throw new EntityNotFoundException("Saving Bank Account Not Created for the Member");
        }
    }

    @Override
    public SavingsBankDeposit getSavingsBankAccountByMemberNumber(String memberNumber) {
        return savingsBankDepositRepository.getSavingsBankAccountByMemberNumber(memberNumber);
    }

    @Override
    public void checkLoanForMember(Member member) {
        List<Loan> memberLoans = new ArrayList<>();
        List<Loan> loans = loanService.findLoanByCustomer(member.getCustomer());
        List<Loan> fixedLoans = loanService.getFixedDepositLoans(member.getCustomer());
        List<Loan> termLoans = loanService.getMemberTermLoans(member.getCustomer());

        if (loans != null) {
            memberLoans.addAll(loans);
        } else if (fixedLoans != null) {
            memberLoans.addAll(fixedLoans);
        } else if (termLoans != null) {
            memberLoans.addAll(termLoans);
        }

        if (memberLoans != null && memberLoans.size() > 0) {
            for (Loan loan : memberLoans) {
                if (!loan.getLoanStatus().equals(LoanStatus.LOAN_CLOSED)) {
                    throw new EntityNotFoundException("Account can not be closed !!Active loan found " + loan.getLoanAccountNumber());
                }
            }
        }
    }

    @Override
    public void checkDepositForMember(Member member) {
        ArrayList list = new ArrayList();
        List<ChildrensDeposit> childrensDeposits = childrensDepositService.getChildrenDepositByMemberNumber(member.getMemberNumber());
        List<DoubleScheme> doubleSchemes = doubleSchemeService.getDoubleSchemeDepositByMemberNumber(member.getMemberNumber());
        List<FixedDeposit> fixedDeposits = fixedDepositService.getFixedDepositByMemberNumber(member.getMemberNumber());
        List<RecurringDeposit> recurringDeposits = recurringDepositService.getRecuringDepositByMemberNumber(member.getMemberNumber());
        List<TermDeposit> termDeposits = termDepositService.getTermDepositByMemberNumber(member.getMemberNumber());

        if (childrensDeposits != null && childrensDeposits.size() > 0) list.add(childrensDeposits);
        if (doubleSchemes != null && doubleSchemes.size() > 0) list.add(doubleSchemes);
        if (fixedDeposits != null && fixedDeposits.size() > 0) list.add(fixedDeposits);
        if (recurringDeposits != null && recurringDeposits.size() > 0) list.add(recurringDeposits);
        if (termDeposits != null && termDeposits.size() > 0) list.add(termDeposits);


        if (list != null && list.size() <= 0) {
            System.out.println("Close account");
        } else {
            throw new EntityNotFoundException("Account can not be closed !!Active Deposits found " + member.getMemberNumber());

        }
    }

    @Override
    public SavingsBankDeposit getAllSavingsBankAccountsByMemberId(Member member) {
        return savingsBankDepositRepository.getSavingBankDepositByMemberId(member);
    }

    @Override
    public List<SavingsBankDeposit> getActiveSavingBankDeposit() {
        return savingsBankDepositRepository.getActiveSavingBankDeposits();
    }
}