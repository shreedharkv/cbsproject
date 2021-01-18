package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.deposit.entity.ChildrensDeposit;
import com.dfq.coeffi.cbs.deposit.repository.ChildrensDepositRepository;
import com.dfq.coeffi.cbs.deposit.service.ChildrensDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
@Transactional
public class ChildrensDepositServiceImpl implements ChildrensDepositService {

    private final ChildrensDepositRepository childrensDepositRepository;

    @Autowired
    public ChildrensDepositServiceImpl(ChildrensDepositRepository childrensDepositRepository) {
        this.childrensDepositRepository = childrensDepositRepository;
    }

    @Override
    public ChildrensDeposit saveDeposit(ChildrensDeposit deposit) {
        return childrensDepositRepository.save(deposit);
    }

    @Override
    public List<ChildrensDeposit> getAllDeposits(Date dateFrom,Date dateTo) {
        return childrensDepositRepository.getAllDeposits(dateFrom,dateTo);
    }

    @Override
    public Optional<ChildrensDeposit> getDepositById(long id) {
        return ofNullable(childrensDepositRepository.findOne(id));
    }

    @Override
    public void deleteDeposit(long id) {
        childrensDepositRepository.delete(id);
    }

    @Override
    public List<ChildrensDeposit> getAllApprovedDeposits() {
        return childrensDepositRepository.getAllApprovedDeposits();
    }

    @Override
    public Optional<ChildrensDeposit> getChildrenDepositByAccountNumber(String accountNumber) {
        return childrensDepositRepository.getChildrenDepositByAccountNumber(accountNumber);
    }

    @Override
    public List<ChildrensDeposit> getChildrenDepositByMemberNumber(String memberNumber) {
        return childrensDepositRepository.getChildrenDepositByMemberNumber(memberNumber);
    }
}
