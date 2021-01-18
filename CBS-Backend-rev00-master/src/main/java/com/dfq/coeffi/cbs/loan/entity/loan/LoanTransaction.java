package com.dfq.coeffi.cbs.loan.entity.loan;

import com.dfq.coeffi.cbs.deposit.entity.TransactionType;
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
 * Model class which tracks all loan related transaction
 * like credit, debit and any other transactions.
 *
 * @see com.dfq.coeffi.cbs.loan.entity.loan.LoanTransaction
 * @author Kapil Kumar
 * @since Feb-2019
 * @version 1.0
 */

@Entity
@Setter
@Getter
public class LoanTransaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private Loan loan;

    private BigDecimal creditAmount;

    private BigDecimal debitAmount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal balance;

    @Temporal(TemporalType.DATE)
    private Date transactionOn;

    @OneToOne
    private User transactionBy;

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

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
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

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Date getTransactionOn() {
		return transactionOn;
	}

	public void setTransactionOn(Date transactionOn) {
		this.transactionOn = transactionOn;
	}

	public User getTransactionBy() {
		return transactionBy;
	}

	public void setTransactionBy(User transactionBy) {
		this.transactionBy = transactionBy;
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