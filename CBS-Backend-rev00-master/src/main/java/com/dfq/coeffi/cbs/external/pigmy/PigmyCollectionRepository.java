package com.dfq.coeffi.cbs.external.pigmy;

import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PigmyCollectionRepository extends JpaRepository<PigmyCollection, Long> {

    List<PigmyCollection> findByUploadedToCbs(boolean uploadedToCbs);
    List<PigmyCollection> findByUploadedOn(Date uploadedOn);
    List<PigmyCollection> findByPigmyAgentAndUploadedOn(PigmyAgent pigmyAgent, Date uploadedOn);
    List<PigmyCollection> findByPigmyAgent(PigmyAgent pigmyAgent);


    @Query("SELECT pc FROM PigmyCollection pc WHERE pc.pigmyAgent.id =:agentId AND pc.uploadedOn BETWEEN :fromDate AND :toDate")
    List<PigmyCollection> getCurrentAgentCollection(@Param("agentId") long agentId,
                                                    @Param("fromDate") Date fromDate,
                                                    @Param("toDate") Date toDate);
}