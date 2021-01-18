package com.dfq.coeffi.cbs.member.entity;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
public class DividendPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String memberNumber;

    private String memberName;

    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @Temporal(TemporalType.DATE)
    private Date postDate;

    private String year;

    private BigDecimal dividendAmount;

    private String sbAccountNumber;

    private String modeOfPayment;

    private String creditAccountNumber;

    private String creditAccountType;

    private String remarks;

    private String debitAccountNumber;

    private String debitAccountType;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL)
    private FinancialYear financialYear;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountHead accountHead;

    @OneToOne(cascade = CascadeType.ALL)
    private Ledger ledger;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(String memberNumber) {
		this.memberNumber = memberNumber;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public BigDecimal getDividendAmount() {
		return dividendAmount;
	}

	public void setDividendAmount(BigDecimal dividendAmount) {
		this.dividendAmount = dividendAmount;
	}

	public String getSbAccountNumber() {
		return sbAccountNumber;
	}

	public void setSbAccountNumber(String sbAccountNumber) {
		this.sbAccountNumber = sbAccountNumber;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public String getCreditAccountNumber() {
		return creditAccountNumber;
	}

	public void setCreditAccountNumber(String creditAccountNumber) {
		this.creditAccountNumber = creditAccountNumber;
	}

	public String getCreditAccountType() {
		return creditAccountType;
	}

	public void setCreditAccountType(String creditAccountType) {
		this.creditAccountType = creditAccountType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDebitAccountNumber() {
		return debitAccountNumber;
	}

	public void setDebitAccountNumber(String debitAccountNumber) {
		this.debitAccountNumber = debitAccountNumber;
	}

	public String getDebitAccountType() {
		return debitAccountType;
	}

	public void setDebitAccountType(String debitAccountType) {
		this.debitAccountType = debitAccountType;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public FinancialYear getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(FinancialYear financialYear) {
		this.financialYear = financialYear;
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