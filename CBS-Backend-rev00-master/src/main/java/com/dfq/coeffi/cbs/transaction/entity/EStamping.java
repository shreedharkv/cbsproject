package com.dfq.coeffi.cbs.transaction.entity;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
public class EStamping implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String remark;

    @OneToOne
    private AccountHead accountHead;

    @OneToOne
    private Ledger ledger;

    private String chequeNo;

    @Temporal(TemporalType.DATE)
    private Date chequeDate;

    private BigDecimal amount;

    private BigDecimal commisionAmount;

    private BigDecimal creditAmount;

    private BigDecimal debitAmount;

    private BigDecimal balance;

    private String transactionType;

    private String transferType;

    @OneToOne
    private User transactionBy;

    @Temporal(TemporalType.DATE)
    private Date transactionOn;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;

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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getCommisionAmount() {
		return commisionAmount;
	}

	public void setCommisionAmount(BigDecimal commisionAmount) {
		this.commisionAmount = commisionAmount;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public User getTransactionBy() {
		return transactionBy;
	}

	public void setTransactionBy(User transactionBy) {
		this.transactionBy = transactionBy;
	}

	public Date getTransactionOn() {
		return transactionOn;
	}

	public void setTransactionOn(Date transactionOn) {
		this.transactionOn = transactionOn;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
    
    
    
    
    
    
    
    
    
}