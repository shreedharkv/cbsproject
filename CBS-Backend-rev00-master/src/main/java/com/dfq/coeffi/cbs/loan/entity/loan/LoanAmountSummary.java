package com.dfq.coeffi.cbs.loan.entity.loan;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class which contains loan amount summary information
 * like outstanding, overdue & recovery loan amount details
 *
 * @see com.dfq.coeffi.cbs.loan.entity.loan.LoanAmountSummary
 * @author Kapil Kumar
 * @since Feb-2019
 * @version 1.0
 */


@Setter
@Getter
@Entity
public class LoanAmountSummary{

    @Id
    private long id;

    @UpdateTimestamp
    private Date lastUpdatedOn;

    @CreationTimestamp
    private Date createdOn;

    private BigDecimal outstandingPrincipal;
    private BigDecimal outstandingInterest;
    private BigDecimal outstandingPenalInterest;

    private BigDecimal overduePrincipal;
    private BigDecimal overdueInterest;
    private BigDecimal overduePenalInterest;

    private BigDecimal recoveryPrincipal;
    private BigDecimal recoveryInterest;
    private BigDecimal recoveryPenalInterest;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}
	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public BigDecimal getOutstandingPrincipal() {
		return outstandingPrincipal;
	}
	public void setOutstandingPrincipal(BigDecimal outstandingPrincipal) {
		this.outstandingPrincipal = outstandingPrincipal;
	}
	public BigDecimal getOutstandingInterest() {
		return outstandingInterest;
	}
	public void setOutstandingInterest(BigDecimal outstandingInterest) {
		this.outstandingInterest = outstandingInterest;
	}
	public BigDecimal getOutstandingPenalInterest() {
		return outstandingPenalInterest;
	}
	public void setOutstandingPenalInterest(BigDecimal outstandingPenalInterest) {
		this.outstandingPenalInterest = outstandingPenalInterest;
	}
	public BigDecimal getOverduePrincipal() {
		return overduePrincipal;
	}
	public void setOverduePrincipal(BigDecimal overduePrincipal) {
		this.overduePrincipal = overduePrincipal;
	}
	public BigDecimal getOverdueInterest() {
		return overdueInterest;
	}
	public void setOverdueInterest(BigDecimal overdueInterest) {
		this.overdueInterest = overdueInterest;
	}
	public BigDecimal getOverduePenalInterest() {
		return overduePenalInterest;
	}
	public void setOverduePenalInterest(BigDecimal overduePenalInterest) {
		this.overduePenalInterest = overduePenalInterest;
	}
	public BigDecimal getRecoveryPrincipal() {
		return recoveryPrincipal;
	}
	public void setRecoveryPrincipal(BigDecimal recoveryPrincipal) {
		this.recoveryPrincipal = recoveryPrincipal;
	}
	public BigDecimal getRecoveryInterest() {
		return recoveryInterest;
	}
	public void setRecoveryInterest(BigDecimal recoveryInterest) {
		this.recoveryInterest = recoveryInterest;
	}
	public BigDecimal getRecoveryPenalInterest() {
		return recoveryPenalInterest;
	}
	public void setRecoveryPenalInterest(BigDecimal recoveryPenalInterest) {
		this.recoveryPenalInterest = recoveryPenalInterest;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}