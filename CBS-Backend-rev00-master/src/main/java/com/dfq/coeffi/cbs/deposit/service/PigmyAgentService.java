package com.dfq.coeffi.cbs.deposit.service;

import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;

import java.util.List;
import java.util.Optional;

public interface PigmyAgentService {

    PigmyAgent savePigmyAgent(PigmyAgent pigmyAgentDetails);
    List<PigmyAgent> getAllPigmyAgent();
    Optional<PigmyAgent> getPigmyAgentById(long id);
    Optional<PigmyAgent> getPigmyAgentByNumber(String agentNumber);
}
