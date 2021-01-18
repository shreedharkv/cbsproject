package com.dfq.coeffi.cbs.transaction.entity;

import com.dfq.coeffi.cbs.deposit.api.DepositNomineeDetails;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
@ToString
public class FundTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date transactionDate;

    private String fromAccountType;

    private String fromAccountNumber;

    private String toAccountType;

    private String toAccountNumber;

    private BigDecimal amount;

    private String transferType;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountHead fromAccountHead;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountHead toAccountHead;

    @OneToOne(cascade = CascadeType.ALL)
    private Ledger ledger;

    @OneToOne
    private User transactionBy;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getFromAccountType() {
		return fromAccountType;
	}

	public void setFromAccountType(String fromAccountType) {
		this.fromAccountType = fromAccountType;
	}

	public String getFromAccountNumber() {
		return fromAccountNumber;
	}

	public void setFromAccountNumber(String fromAccountNumber) {
		this.fromAccountNumber = fromAccountNumber;
	}

	public String getToAccountType() {
		return toAccountType;
	}

	public void setToAccountType(String toAccountType) {
		this.toAccountType = toAccountType;
	}

	public String getToAccountNumber() {
		return toAccountNumber;
	}

	public void setToAccountNumber(String toAccountNumber) {
		this.toAccountNumber = toAccountNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public AccountHead getFromAccountHead() {
		return fromAccountHead;
	}

	public void setFromAccountHead(AccountHead fromAccountHead) {
		this.fromAccountHead = fromAccountHead;
	}

	public AccountHead getToAccountHead() {
		return toAccountHead;
	}

	public void setToAccountHead(AccountHead toAccountHead) {
		this.toAccountHead = toAccountHead;
	}

	public Ledger getLedger() {
		return ledger;
	}

	public void setLedger(Ledger ledger) {
		this.ledger = ledger;
	}

	public User getTransactionBy() {
		return transactionBy;
	}

	public void setTransactionBy(User transactionBy) {
		this.transactionBy = transactionBy;
	}
    
    
    
    
    
    
    
    
    
    

}
