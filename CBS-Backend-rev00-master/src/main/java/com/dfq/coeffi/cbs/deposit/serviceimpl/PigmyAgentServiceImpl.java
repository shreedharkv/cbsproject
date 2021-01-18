package com.dfq.coeffi.cbs.deposit.serviceimpl;

import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;
import com.dfq.coeffi.cbs.deposit.repository.PigmyAgentRepository;
import com.dfq.coeffi.cbs.deposit.service.PigmyAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
@Transactional
public class PigmyAgentServiceImpl implements PigmyAgentService {

    @Autowired
    private final PigmyAgentRepository pigmyAgentRepository;

    public PigmyAgentServiceImpl(PigmyAgentRepository pigmyAgentRepository){
        this.pigmyAgentRepository = pigmyAgentRepository;
    }

    @Override
    public PigmyAgent savePigmyAgent(PigmyAgent pigmyAgentDetails) {
        return pigmyAgentRepository.save(pigmyAgentDetails);
    }

    @Override
    public List<PigmyAgent> getAllPigmyAgent() {
        return pigmyAgentRepository.findAll();
    }

    @Override
    public Optional<PigmyAgent> getPigmyAgentById(long id) {
        return ofNullable(pigmyAgentRepository.findOne(id));
    }

    @Override
    public Optional<PigmyAgent> getPigmyAgentByNumber(String agentNumber) {
        return  pigmyAgentRepository.getPigmyAgentByNumber(agentNumber);
    }

}
