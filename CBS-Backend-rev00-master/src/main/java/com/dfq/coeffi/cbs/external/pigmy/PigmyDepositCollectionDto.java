package com.dfq.coeffi.cbs.external.pigmy;

import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class PigmyDepositCollectionDto {

    private PigmyDeposit pigmyDeposit;

    private String collectedAmount;

    private Date collectedOn;

    private PigmyAgent pigmyAgent;

    private String accountNumber;


    private String modeOfPayment;
    private String chequeNumber;
    private Date chequeDate;
    private long amount;
}
