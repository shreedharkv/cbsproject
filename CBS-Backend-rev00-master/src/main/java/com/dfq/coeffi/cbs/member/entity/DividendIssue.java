package com.dfq.coeffi.cbs.member.entity;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
public class DividendIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String dividendYear;

    private String summary;

    @Temporal(TemporalType.DATE)
    private Date dateFrom;

    @Temporal(TemporalType.DATE)
    private Date dateTo;

    private BigDecimal rateOfInterest;

    @Temporal(TemporalType.DATE)
    private Date approvedDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private BigDecimal dividendAmount;

    private String warrantNumber;

    @OneToOne(cascade = CascadeType.ALL)
    private FinancialYear financialYear;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDividendYear() {
		return dividendYear;
	}

	public void setDividendYear(String dividendYear) {
		this.dividendYear = dividendYear;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public BigDecimal getRateOfInterest() {
		return rateOfInterest;
	}

	public void setRateOfInterest(BigDecimal rateOfInterest) {
		this.rateOfInterest = rateOfInterest;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
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

	public BigDecimal getDividendAmount() {
		return dividendAmount;
	}

	public void setDividendAmount(BigDecimal dividendAmount) {
		this.dividendAmount = dividendAmount;
	}

	public String getWarrantNumber() {
		return warrantNumber;
	}

	public void setWarrantNumber(String warrantNumber) {
		this.warrantNumber = warrantNumber;
	}

	public FinancialYear getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(FinancialYear financialYear) {
		this.financialYear = financialYear;
	}
    
    
    
    
    
    
    
    
    
    
}