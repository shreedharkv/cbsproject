package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.deposit.entity.TermDeposit;
import com.dfq.coeffi.cbs.deposit.repository.TermDepositRepository;
import com.dfq.coeffi.cbs.deposit.service.TermDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Transactional
@Service
public class TermDepositServiceImpl implements TermDepositService {

    private final TermDepositRepository termDepositRepository;

    @Autowired
    public TermDepositServiceImpl(TermDepositRepository termDepositRepository) {
        this.termDepositRepository = termDepositRepository;
    }

    @Override
    public TermDeposit saveTermDeposit(TermDeposit termDeposit) {
        return termDepositRepository.save(termDeposit);
    }

    @Override
    public List<TermDeposit> getAllTermDeposits(Date dateFrom,Date dateTo) {
        return termDepositRepository.getAllTermDeposits(dateFrom,dateTo);
    }

    @Override
    public Optional<TermDeposit> getTermDepositById(long id) {
        return ofNullable(termDepositRepository.findOne(id));
    }

    @Override
    public void deleteTermDeposit(long id) {
        termDepositRepository.delete(id);
    }

    @Override
    public Optional<TermDeposit> getTermDepositByAccountNumber(String accountNumber) {
        return termDepositRepository.getTermDepositByAccountNumber(accountNumber);
    }

    @Override
    public List<TermDeposit> getTermDepositByMemberNumber(String memberNumber) {
        return termDepositRepository.getTermDepositByMemberNumber(memberNumber);
    }
}