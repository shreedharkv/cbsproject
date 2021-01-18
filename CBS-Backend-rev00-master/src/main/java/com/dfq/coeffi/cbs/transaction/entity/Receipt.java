package com.dfq.coeffi.cbs.transaction.entity;

import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String accountNumber;

    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    private Date transactionOn;

    @CreationTimestamp
    private Date createdOn;

    private BigDecimal creditAmount;

    private BigDecimal balance;

    private String transactionType;

    private String transferType;

    private String depositType;

    private String chequeNumber;

    private String remarks;

    private String paymentNumber;

    private Date chequeDate;

    @OneToOne
    private User transactionBy;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
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

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
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

	public String getDepositType() {
		return depositType;
	}

	public void setDepositType(String depositType) {
		this.depositType = depositType;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(String paymentNumber) {
		this.paymentNumber = paymentNumber;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public User getTransactionBy() {
		return transactionBy;
	}

	public void setTransactionBy(User transactionBy) {
		this.transactionBy = transactionBy;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}