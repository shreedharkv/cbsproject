package com.dfq.coeffi.cbs.external.pigmy;

import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AgentCollectionDto {

    private BigDecimal collectedAmount;

    private PigmyAgent pigmyAgent;
}
