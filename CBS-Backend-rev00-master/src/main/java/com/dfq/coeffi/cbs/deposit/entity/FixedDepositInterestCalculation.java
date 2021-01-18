package com.dfq.coeffi.cbs.deposit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@ToString
public class FixedDepositInterestCalculation {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String accountNumber;

    private BigDecimal depositAmount;

    private float interestRate;

    private BigDecimal interestAmount;

    private String depositType;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;

    private Boolean isInterestAdded;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}

	public float getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(float interestRate) {
		this.interestRate = interestRate;
	}

	public BigDecimal getInterestAmount() {
		return interestAmount;
	}

	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
	}

	public String getDepositType() {
		return depositType;
	}

	public void setDepositType(String depositType) {
		this.depositType = depositType;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Boolean getIsInterestAdded() {
		return isInterestAdded;
	}

	public void setIsInterestAdded(Boolean isInterestAdded) {
		this.isInterestAdded = isInterestAdded;
	}
    
    
    
    
    
    
    
    
    
    
    
    
}
