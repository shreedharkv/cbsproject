package com.dfq.coeffi.cbs.deposit.entity;


import com.dfq.coeffi.cbs.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
public class DepositTransaction {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String accountNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private DepositType depositType;

    @Column(length = 20)
    private BigDecimal depositAmount;

    @Column(length = 45)
    private BigDecimal interestAmount;

    @Column(length = 20)
    private BigDecimal withDrawAmount;

    @Column(length = 20)
    private BigDecimal balanceAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private TransactionType transactionType;

    @Column(length = 10)
    private boolean status;

    @OneToOne(cascade = CascadeType.ALL)
    private Member member;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public DepositType getDepositType() {
		return depositType;
	}

	public void setDepositType(DepositType depositType) {
		this.depositType = depositType;
	}

	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}

	public BigDecimal getInterestAmount() {
		return interestAmount;
	}

	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
	}

	public BigDecimal getWithDrawAmount() {
		return withDrawAmount;
	}

	public void setWithDrawAmount(BigDecimal withDrawAmount) {
		this.withDrawAmount = withDrawAmount;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
    
    
    
    
    
    
    
    
    
    
    
    
}
