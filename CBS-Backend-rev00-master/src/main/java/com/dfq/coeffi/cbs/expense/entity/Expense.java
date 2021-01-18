package com.dfq.coeffi.cbs.expense.entity;

import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;

    private BigDecimal amount;

    private String transactionType;

    private String transferType;

    @OneToOne
    private AccountHead accountHead;

    @OneToOne
    private Ledger ledger;

    private String otherDescription;
    private String remunerationType;

    private BigDecimal tds;
    private BigDecimal collectedAmount;
    private BigDecimal commissionAmount;

    private long pigmyAgentId;

    @OneToOne
    private PigmyAgent collectionAgent;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public String getOtherDescription() {
		return otherDescription;
	}

	public void setOtherDescription(String otherDescription) {
		this.otherDescription = otherDescription;
	}

	public String getRemunerationType() {
		return remunerationType;
	}

	public void setRemunerationType(String remunerationType) {
		this.remunerationType = remunerationType;
	}

	public BigDecimal getTds() {
		return tds;
	}

	public void setTds(BigDecimal tds) {
		this.tds = tds;
	}

	public BigDecimal getCollectedAmount() {
		return collectedAmount;
	}

	public void setCollectedAmount(BigDecimal collectedAmount) {
		this.collectedAmount = collectedAmount;
	}

	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}

	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	public long getPigmyAgentId() {
		return pigmyAgentId;
	}

	public void setPigmyAgentId(long pigmyAgentId) {
		this.pigmyAgentId = pigmyAgentId;
	}

	public PigmyAgent getCollectionAgent() {
		return collectionAgent;
	}

	public void setCollectionAgent(PigmyAgent collectionAgent) {
		this.collectionAgent = collectionAgent;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}