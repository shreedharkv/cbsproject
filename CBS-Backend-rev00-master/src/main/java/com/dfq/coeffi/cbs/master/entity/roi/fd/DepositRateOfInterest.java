package com.dfq.coeffi.cbs.master.entity.roi.fd;

import com.dfq.coeffi.cbs.deposit.entity.DepositType;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name="deposit_interest_master")
public class DepositRateOfInterest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private DepositType depositType;

    private float regularRateOfInterest;

    private float penalRateOfInterest;

    @Temporal(TemporalType.DATE)
    private Date effectFrom;

    private boolean active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public DepositType getDepositType() {
		return depositType;
	}

	public void setDepositType(DepositType depositType) {
		this.depositType = depositType;
	}

	public float getRegularRateOfInterest() {
		return regularRateOfInterest;
	}

	public void setRegularRateOfInterest(float regularRateOfInterest) {
		this.regularRateOfInterest = regularRateOfInterest;
	}

	public float getPenalRateOfInterest() {
		return penalRateOfInterest;
	}

	public void setPenalRateOfInterest(float penalRateOfInterest) {
		this.penalRateOfInterest = penalRateOfInterest;
	}

	public Date getEffectFrom() {
		return effectFrom;
	}

	public void setEffectFrom(Date effectFrom) {
		this.effectFrom = effectFrom;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    
    
    
    
    
    
    
    
    
    
    

}