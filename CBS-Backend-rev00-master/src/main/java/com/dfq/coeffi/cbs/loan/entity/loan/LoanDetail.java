package com.dfq.coeffi.cbs.loan.entity.loan;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.master.entity.roi.loan.LoanRateOfInterest;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Embeddable
public class LoanDetail implements Serializable {

    private BigDecimal sanctionedAmount;

    private BigDecimal appliedAmount;

    private BigDecimal recommendedAmount;

    private BigDecimal rateOfInterest;

    private BigDecimal balanceAmount;

    private BigDecimal repaidAmount;

    private BigDecimal totalRecovered;

    private boolean priority;

    private boolean approved;

    @OneToOne
    private User approvedBy;

    @OneToOne
    private User loanClosedBy;

    @Temporal(TemporalType.DATE)
    private Date loanClosedOn;

    @Temporal(TemporalType.DATE)
    private Date approvedOn;

    @ManyToOne(cascade = CascadeType.ALL)
    private GoldDetails goldDetails;

    private long loanCode;

    @ManyToOne
    private LoanRateOfInterest loanRateOfInterest;

    @Column(name="customer_ref_id")
    private long customerId;

    @ManyToOne(cascade = CascadeType.ALL)
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL)
    private LADDetails ladDetails;

    @OneToOne(cascade = CascadeType.ALL)
    private TermDetails termDetails;

    @OneToOne(cascade = CascadeType.ALL)
    private LoanAmountSummary loanAmountSummary;

    private BigDecimal payablePrincipalAmount;

    private BigDecimal payableInterestAmount;

    private BigDecimal paidPrincipalAmount;

    private BigDecimal paidInterestAmount;

    private BigDecimal paidPenalInterestAmount;

    private BigDecimal applicationFee;

	public BigDecimal getSanctionedAmount() {
		return sanctionedAmount;
	}

	public void setSanctionedAmount(BigDecimal sanctionedAmount) {
		this.sanctionedAmount = sanctionedAmount;
	}

	public BigDecimal getAppliedAmount() {
		return appliedAmount;
	}

	public void setAppliedAmount(BigDecimal appliedAmount) {
		this.appliedAmount = appliedAmount;
	}

	public BigDecimal getRecommendedAmount() {
		return recommendedAmount;
	}

	public void setRecommendedAmount(BigDecimal recommendedAmount) {
		this.recommendedAmount = recommendedAmount;
	}

	public BigDecimal getRateOfInterest() {
		return rateOfInterest;
	}

	public void setRateOfInterest(BigDecimal rateOfInterest) {
		this.rateOfInterest = rateOfInterest;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public BigDecimal getRepaidAmount() {
		return repaidAmount;
	}

	public void setRepaidAmount(BigDecimal repaidAmount) {
		this.repaidAmount = repaidAmount;
	}

	public BigDecimal getTotalRecovered() {
		return totalRecovered;
	}

	public void setTotalRecovered(BigDecimal totalRecovered) {
		this.totalRecovered = totalRecovered;
	}

	public boolean isPriority() {
		return priority;
	}

	public void setPriority(boolean priority) {
		this.priority = priority;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public User getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	public User getLoanClosedBy() {
		return loanClosedBy;
	}

	public void setLoanClosedBy(User loanClosedBy) {
		this.loanClosedBy = loanClosedBy;
	}

	public Date getLoanClosedOn() {
		return loanClosedOn;
	}

	public void setLoanClosedOn(Date loanClosedOn) {
		this.loanClosedOn = loanClosedOn;
	}

	public Date getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}

	public GoldDetails getGoldDetails() {
		return goldDetails;
	}

	public void setGoldDetails(GoldDetails goldDetails) {
		this.goldDetails = goldDetails;
	}

	public long getLoanCode() {
		return loanCode;
	}

	public void setLoanCode(long loanCode) {
		this.loanCode = loanCode;
	}

	public LoanRateOfInterest getLoanRateOfInterest() {
		return loanRateOfInterest;
	}

	public void setLoanRateOfInterest(LoanRateOfInterest loanRateOfInterest) {
		this.loanRateOfInterest = loanRateOfInterest;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LADDetails getLadDetails() {
		return ladDetails;
	}

	public void setLadDetails(LADDetails ladDetails) {
		this.ladDetails = ladDetails;
	}

	public TermDetails getTermDetails() {
		return termDetails;
	}

	public void setTermDetails(TermDetails termDetails) {
		this.termDetails = termDetails;
	}

	public LoanAmountSummary getLoanAmountSummary() {
		return loanAmountSummary;
	}

	public void setLoanAmountSummary(LoanAmountSummary loanAmountSummary) {
		this.loanAmountSummary = loanAmountSummary;
	}

	public BigDecimal getPayablePrincipalAmount() {
		return payablePrincipalAmount;
	}

	public void setPayablePrincipalAmount(BigDecimal payablePrincipalAmount) {
		this.payablePrincipalAmount = payablePrincipalAmount;
	}

	public BigDecimal getPayableInterestAmount() {
		return payableInterestAmount;
	}

	public void setPayableInterestAmount(BigDecimal payableInterestAmount) {
		this.payableInterestAmount = payableInterestAmount;
	}

	public BigDecimal getPaidPrincipalAmount() {
		return paidPrincipalAmount;
	}

	public void setPaidPrincipalAmount(BigDecimal paidPrincipalAmount) {
		this.paidPrincipalAmount = paidPrincipalAmount;
	}

	public BigDecimal getPaidInterestAmount() {
		return paidInterestAmount;
	}

	public void setPaidInterestAmount(BigDecimal paidInterestAmount) {
		this.paidInterestAmount = paidInterestAmount;
	}

	public BigDecimal getPaidPenalInterestAmount() {
		return paidPenalInterestAmount;
	}

	public void setPaidPenalInterestAmount(BigDecimal paidPenalInterestAmount) {
		this.paidPenalInterestAmount = paidPenalInterestAmount;
	}

	public BigDecimal getApplicationFee() {
		return applicationFee;
	}

	public void setApplicationFee(BigDecimal applicationFee) {
		this.applicationFee = applicationFee;
	}
    
    
    
    
    
    
    
    
    
    
    
    

}