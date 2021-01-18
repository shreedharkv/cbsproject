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
 * Model class which contains loan disbursement details
 * like disbursed amount, amount credited account,
 * amount debited account details
 *
 * @see com.dfq.coeffi.cbs.loan.entity.loan.LoanDisbursement
 * @author Kapil Kumar
 * @since Feb-2019
 * @version 1.0
 */

@Setter
@Getter
@Entity
public class LoanDisbursement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long debitAccountCode;

    private BigDecimal disbursedAmount;

    private BigDecimal otherCharges;

    private long creditAccountCode;

    private String creditMode;

    private String chequeNo;

    @Temporal(TemporalType.DATE)
    private Date chequeDate;

    private String remark;

    @OneToOne
    private User disbursedBy;

    @Temporal(TemporalType.DATE)
    private Date disbursedOn;

    @ManyToOne
    private AccountHead creditAccount;

    @ManyToOne
    private AccountHead debitAccount;

    @OneToOne
    private AccountHead accountHead;

    @OneToOne
    private Ledger ledger;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDebitAccountCode() {
		return debitAccountCode;
	}

	public void setDebitAccountCode(long debitAccountCode) {
		this.debitAccountCode = debitAccountCode;
	}

	public BigDecimal getDisbursedAmount() {
		return disbursedAmount;
	}

	public void setDisbursedAmount(BigDecimal disbursedAmount) {
		this.disbursedAmount = disbursedAmount;
	}

	public BigDecimal getOtherCharges() {
		return otherCharges;
	}

	public void setOtherCharges(BigDecimal otherCharges) {
		this.otherCharges = otherCharges;
	}

	public long getCreditAccountCode() {
		return creditAccountCode;
	}

	public void setCreditAccountCode(long creditAccountCode) {
		this.creditAccountCode = creditAccountCode;
	}

	public String getCreditMode() {
		return creditMode;
	}

	public void setCreditMode(String creditMode) {
		this.creditMode = creditMode;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getDisbursedBy() {
		return disbursedBy;
	}

	public void setDisbursedBy(User disbursedBy) {
		this.disbursedBy = disbursedBy;
	}

	public Date getDisbursedOn() {
		return disbursedOn;
	}

	public void setDisbursedOn(Date disbursedOn) {
		this.disbursedOn = disbursedOn;
	}

	public AccountHead getCreditAccount() {
		return creditAccount;
	}

	public void setCreditAccount(AccountHead creditAccount) {
		this.creditAccount = creditAccount;
	}

	public AccountHead getDebitAccount() {
		return debitAccount;
	}

	public void setDebitAccount(AccountHead debitAccount) {
		this.debitAccount = debitAccount;
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
    
    
    
    
    
    
    
    
    
    
    
    
    
}