package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.deposit.entity.DepositAccountNumberMaster;
import com.dfq.coeffi.cbs.deposit.repository.DepositAccountNumberMasterRepository;
import com.dfq.coeffi.cbs.deposit.service.DepositAccountNumberMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import static java.util.Optional.ofNullable;


@Service
public class DepositAccountNumberMasterImpl implements DepositAccountNumberMasterService {

    private final DepositAccountNumberMasterRepository depositAccountNumberMasterRepository;

    @Autowired
    public DepositAccountNumberMasterImpl(DepositAccountNumberMasterRepository depositAccountNumberMasterRepository)
    {
        this.depositAccountNumberMasterRepository = depositAccountNumberMasterRepository;
    }

    @Override
    public Optional<DepositAccountNumberMaster> getDepositAccountNumberMasterById(long id) {
        return ofNullable(depositAccountNumberMasterRepository.findOne(id));
    }

    @Override
    public DepositAccountNumberMaster saveDepositAccountNumberMaster(DepositAccountNumberMaster depositAccountNumberMaster) {
        return depositAccountNumberMasterRepository.save(depositAccountNumberMaster);
    }
}
