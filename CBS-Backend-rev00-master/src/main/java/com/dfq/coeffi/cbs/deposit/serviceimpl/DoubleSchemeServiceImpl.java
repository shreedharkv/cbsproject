package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.deposit.entity.DoubleScheme;
import com.dfq.coeffi.cbs.deposit.repository.DoubleSchemeRepository;
import com.dfq.coeffi.cbs.deposit.service.DoubleSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
@Transactional
public class DoubleSchemeServiceImpl implements DoubleSchemeService {

    private final DoubleSchemeRepository doubleSchemeRepository;

    @Autowired
    public DoubleSchemeServiceImpl(DoubleSchemeRepository doubleSchemeRepository) {
        this.doubleSchemeRepository = doubleSchemeRepository;
    }

    @Override
    public DoubleScheme saveDoubleSchemeDeposit(DoubleScheme doubleScheme) {
        return doubleSchemeRepository.save(doubleScheme);
    }

    @Override
    public List<DoubleScheme> getAllDoubleSchemeDeposits(Date dateFrom,Date dateTo) {
        return doubleSchemeRepository.getAllDoubleSchemeDeposits(dateFrom,dateTo);
    }

    @Override
    public Optional<DoubleScheme> getDoubleSchemeDepositById(long id) {
        return ofNullable(doubleSchemeRepository.findOne(id));
    }

    @Override
    public void deleteDoubleSchemeDeposit(long id) {
        doubleSchemeRepository.delete(id);
    }

    @Override
    public Optional<DoubleScheme> getDoubleSchemeByAccountNumber(String accountNumber) {
        return doubleSchemeRepository.getDoubleSchemeByAccountNumber(accountNumber);
    }

    @Override
    public List<DoubleScheme> getDoubleSchemeDepositByMemberNumber(String memberNumber) {
        return doubleSchemeRepository.getDoubleSchemeByMemberNumber(memberNumber);
    }
}
