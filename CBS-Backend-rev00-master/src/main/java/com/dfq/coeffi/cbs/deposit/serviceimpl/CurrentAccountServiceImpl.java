package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.deposit.entity.CurrentAccount;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import com.dfq.coeffi.cbs.deposit.repository.CurrentAccountRepository;
import com.dfq.coeffi.cbs.deposit.service.CurrentAccountService;
import com.dfq.coeffi.cbs.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
@Transactional
public class CurrentAccountServiceImpl implements CurrentAccountService {

    private final CurrentAccountRepository currentAccountRepository;

    @Autowired
    public CurrentAccountServiceImpl(CurrentAccountRepository currentAccountRepository) {
        this.currentAccountRepository = currentAccountRepository;
    }

    @Override
    public CurrentAccount saveCurrentAccount(CurrentAccount currentAccount) {
        return currentAccountRepository.save(currentAccount);
    }

    @Override
    public List<CurrentAccount> getAllCurrentAccountDeposits(Date dateFrom,Date dateTo) {
        return currentAccountRepository.getAllCurrentAccountDeposits(dateFrom,dateTo);
    }

    @Override
    public Optional<CurrentAccount> getCurrentAccountDepositById(long id) {
        return ofNullable(currentAccountRepository.findOne(id));
    }

    @Override
    public void deleteCurrentAccountDeposit(long id) {
        currentAccountRepository.delete(id);
    }

    @Override
    public List<CurrentAccount> getAllApprovedCurrentAccountDeposits() {
        return null;
    }

    @Override
    public List<CurrentAccount> getAllCurrentAccountsByMember(Member member) {
        return currentAccountRepository.getCurrentAccountByMember(member);
    }

    @Override
    public Optional<CurrentAccount> getCurrentAccountDepositByAccountNumber(String accountNumber) {
        return currentAccountRepository.getCurrentAccountDepositByAccountNumber(accountNumber);
    }
}
