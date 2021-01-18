package com.dfq.coeffi.cbs.loan.entity.loan;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model class which contains loan scheduled information
 * like EMI details, No. Of Installments, Recovery date etc
 *
 * @see com.dfq.coeffi.cbs.loan.entity.loan.LoanSchedule
 * @author Kapil Kumar
 * @since Feb-2019
 * @version 1.0
 */


@Setter
@Getter
@Entity
public class LoanSchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int totalInstallments;

    private String frequency;

    @Temporal(TemporalType.DATE)
    private Date repaymentStartOn;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private Date scheduledOn;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<LoanInstallments> installments = new ArrayList<>();

    @Temporal(TemporalType.DATE)
    private Date recoveryDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getTotalInstallments() {
		return totalInstallments;
	}

	public void setTotalInstallments(int totalInstallments) {
		this.totalInstallments = totalInstallments;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public Date getRepaymentStartOn() {
		return repaymentStartOn;
	}

	public void setRepaymentStartOn(Date repaymentStartOn) {
		this.repaymentStartOn = repaymentStartOn;
	}

	public Date getScheduledOn() {
		return scheduledOn;
	}

	public void setScheduledOn(Date scheduledOn) {
		this.scheduledOn = scheduledOn;
	}

	public List<LoanInstallments> getInstallments() {
		return installments;
	}

	public void setInstallments(List<LoanInstallments> installments) {
		this.installments = installments;
	}

	public Date getRecoveryDate() {
		return recoveryDate;
	}

	public void setRecoveryDate(Date recoveryDate) {
		this.recoveryDate = recoveryDate;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}