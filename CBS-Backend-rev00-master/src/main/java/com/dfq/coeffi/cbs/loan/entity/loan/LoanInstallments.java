package com.dfq.coeffi.cbs.loan.entity.loan;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class which maintain loan installments details
 * like EMI, Interest, Penal Interest, Paid amount and
 * Installment status.
 *
 * @author Kapil Kumar
 * @version 1.0
 * @see com.dfq.coeffi.cbs.loan.entity.loan.LoanInstallments
 * @since Feb-2019
 */

@Setter
@Getter
@Entity
public class LoanInstallments implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private BigDecimal principleAmount;

    private BigDecimal emiAmount;

    private BigDecimal paidAmount;

    private String loanAccountNumber;

    private BigDecimal interestAmount;

    private BigDecimal penalInterestAmount;

    @Temporal(TemporalType.DATE)
    private Date paymentDate;

    private boolean paidStatus;

    @Enumerated(EnumType.STRING)
    private LoanEmiStatus loanEmiStatus;

    private int installmentNumber;

    @Temporal(TemporalType.DATE)
    private Date dueDate;

    private BigDecimal payablePrincipal;
    private BigDecimal payableInterest;
    private BigDecimal payablePenalInterest;

    private BigDecimal paidPrincipal;
    private BigDecimal paidInterest;
    private BigDecimal paidPenalInterest;
    private BigDecimal overdueAmount;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public BigDecimal getPrincipleAmount() {
		return principleAmount;
	}
	public void setPrincipleAmount(BigDecimal principleAmount) {
		this.principleAmount = principleAmount;
	}
	public BigDecimal getEmiAmount() {
		return emiAmount;
	}
	public void setEmiAmount(BigDecimal emiAmount) {
		this.emiAmount = emiAmount;
	}
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}
	public String getLoanAccountNumber() {
		return loanAccountNumber;
	}
	public void setLoanAccountNumber(String loanAccountNumber) {
		this.loanAccountNumber = loanAccountNumber;
	}
	public BigDecimal getInterestAmount() {
		return interestAmount;
	}
	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
	}
	public BigDecimal getPenalInterestAmount() {
		return penalInterestAmount;
	}
	public void setPenalInterestAmount(BigDecimal penalInterestAmount) {
		this.penalInterestAmount = penalInterestAmount;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public boolean isPaidStatus() {
		return paidStatus;
	}
	public void setPaidStatus(boolean paidStatus) {
		this.paidStatus = paidStatus;
	}
	public LoanEmiStatus getLoanEmiStatus() {
		return loanEmiStatus;
	}
	public void setLoanEmiStatus(LoanEmiStatus loanEmiStatus) {
		this.loanEmiStatus = loanEmiStatus;
	}
	public int getInstallmentNumber() {
		return installmentNumber;
	}
	public void setInstallmentNumber(int installmentNumber) {
		this.installmentNumber = installmentNumber;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public BigDecimal getPayablePrincipal() {
		return payablePrincipal;
	}
	public void setPayablePrincipal(BigDecimal payablePrincipal) {
		this.payablePrincipal = payablePrincipal;
	}
	public BigDecimal getPayableInterest() {
		return payableInterest;
	}
	public void setPayableInterest(BigDecimal payableInterest) {
		this.payableInterest = payableInterest;
	}
	public BigDecimal getPayablePenalInterest() {
		return payablePenalInterest;
	}
	public void setPayablePenalInterest(BigDecimal payablePenalInterest) {
		this.payablePenalInterest = payablePenalInterest;
	}
	public BigDecimal getPaidPrincipal() {
		return paidPrincipal;
	}
	public void setPaidPrincipal(BigDecimal paidPrincipal) {
		this.paidPrincipal = paidPrincipal;
	}
	public BigDecimal getPaidInterest() {
		return paidInterest;
	}
	public void setPaidInterest(BigDecimal paidInterest) {
		this.paidInterest = paidInterest;
	}
	public BigDecimal getPaidPenalInterest() {
		return paidPenalInterest;
	}
	public void setPaidPenalInterest(BigDecimal paidPenalInterest) {
		this.paidPenalInterest = paidPenalInterest;
	}
	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}
	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}