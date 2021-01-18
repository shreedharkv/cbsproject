package com.dfq.coeffi.cbs.transaction.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Setter
@Getter
@Entity
public class EodTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private BigDecimal creditAmount;
    private BigDecimal debitAmount;
    private BigDecimal balanceAsOn;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public BigDecimal getBalanceAsOn() {
		return balanceAsOn;
	}
	public void setBalanceAsOn(BigDecimal balanceAsOn) {
		this.balanceAsOn = balanceAsOn;
	}
	public BigDecimal getOpeningBalance() {
		return openingBalance;
	}
	public void setOpeningBalance(BigDecimal openingBalance) {
		this.openingBalance = openingBalance;
	}
	public BigDecimal getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}
    
    
    
    
    
    
    
}