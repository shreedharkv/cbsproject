package com.dfq.coeffi.cbs.loan.entity.loan;

import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Model class which track any other expense charges
 * against the loan.
 *
 * @see com.dfq.coeffi.cbs.loan.entity.loan.LoanOtherCharges
 * @author Kapil Kumar
 * @since Feb-2019
 * @version 1.0
 */

@Entity
@Setter
@Getter
public class LoanOtherCharges {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String remark;

    private BigDecimal amount;
    private String chequeNo;
    private String debitType;
    private String loanAccountNo;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountHead creditAccount;

    @OneToOne(cascade = CascadeType.ALL)
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getDebitType() {
		return debitType;
	}

	public void setDebitType(String debitType) {
		this.debitType = debitType;
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