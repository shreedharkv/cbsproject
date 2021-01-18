package com.dfq.coeffi.cbs.external.pigmy;


import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name="pigmy_collection")
public class PigmyCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "collected_amount")
    private BigDecimal collectedAmount;

    @Column(name = "ref_id")
    private String refId;

    @Column(name = "ref_name")
    private String refName;

    @Column(name = "agent_id")
    private String  agentId;

    @Column(name = "collected_device_date")
    private String  collectedOn;

    @Column(name = "collected_device_time")
    private String  collectedTime;

    @Column(name = "balance_as_per_device")
    private BigDecimal balanceAsPerDevice;

    @Temporal(TemporalType.DATE)
    private Date uploadedOn;

    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "uploaded_to_cbs")
    private boolean uploadedToCbs;

    @OneToOne
    private PigmyDeposit pigmyDeposit;

    @OneToOne
    private PigmyAgent pigmyAgent;
}