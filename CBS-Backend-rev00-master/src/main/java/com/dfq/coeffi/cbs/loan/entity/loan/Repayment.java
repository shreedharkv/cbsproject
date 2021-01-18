package com.dfq.coeffi.cbs.loan.entity.loan;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class which contains repayment information like
 * Paid emi, with which account head and against which
 * account number repayments done.
 *
 * @see com.dfq.coeffi.cbs.loan.entity.loan.Repayment
 * @author Kapil Kumar
 * @since Feb-2019
 * @version 1.0
 */

@Setter
@Getter
@Entity
public class Repayment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String remark;

    private String chequeNo;
    private String creditType;
    private String loanAccountNo;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountHead creditAccount;

    private BigDecimal paidInterest;
    private BigDecimal paidPenalInterest;
    private BigDecimal paidPrincipal;
    private BigDecimal receivedAmount;

    @OneToOne
    private User repaymentDoneBy;

    @Temporal(TemporalType.DATE)
    private Date repaymentOn;

    @OneToOne
    private AccountHead accountHead;

    @OneToOne
    private Ledger ledger;

    private BigDecimal overdueAmount;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getCreditType() {
		return creditType;
	}

	public void setCreditType(String creditType) {
		this.creditType = creditType;
	}

	public String getLoanAccountNo() {
		return loanAccountNo;
	}

	public void setLoanAccountNo(String loanAccountNo) {
		this.loanAccountNo = loanAccountNo;
	}

	public AccountHead getCreditAccount() {
		return creditAccount;
	}

	public void setCreditAccount(AccountHead creditAccount) {
		this.creditAccount = creditAccount;
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

	public BigDecimal getPaidPrincipal() {
		return paidPrincipal;
	}

	public void setPaidPrincipal(BigDecimal paidPrincipal) {
		this.paidPrincipal = paidPrincipal;
	}

	public BigDecimal getReceivedAmount() {
		return receivedAmount;
	}

	public void setReceivedAmount(BigDecimal receivedAmount) {
		this.receivedAmount = receivedAmount;
	}

	public User getRepaymentDoneBy() {
		return repaymentDoneBy;
	}

	public void setRepaymentDoneBy(User repaymentDoneBy) {
		this.repaymentDoneBy = repaymentDoneBy;
	}

	public Date getRepaymentOn() {
		return repaymentOn;
	}

	public void setRepaymentOn(Date repaymentOn) {
		this.repaymentOn = repaymentOn;
	}

	public AccountHead getAccountHead() {
		return accountHead;
	}

	public void setAccountHead(AccountHead accountHead) {
		this.accountHead = accountHead;
	}

	public Ledger getLedger() {
		return ledger;
	}

	public void setLedger(Ledger ledger) {
		this.ledger = ledger;
	}

	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}