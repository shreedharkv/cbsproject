package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface PigmyAgentRepository extends JpaRepository<PigmyAgent,Long> {

    @Query("SELECT pigmyAgent FROM PigmyAgent pigmyAgent WHERE pigmyAgent.agentNumber =:agentNumber")
    Optional<PigmyAgent> getPigmyAgentByNumber(@Param("agentNumber") String agentNumber);
}
