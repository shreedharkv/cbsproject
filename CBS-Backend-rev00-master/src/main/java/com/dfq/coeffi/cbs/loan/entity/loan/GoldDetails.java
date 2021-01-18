package com.dfq.coeffi.cbs.loan.entity.loan;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Model class which contains gold loan information
 * like No of bags, Net weight, Gross Weight, Rate per
 * gram etc
 *
 * @see com.dfq.coeffi.cbs.loan.entity.loan.GoldDetails
 * @author Kapil Kumar
 * @since Feb-2019
 * @version 1.0
 */

@Setter
@Getter
@Entity
public class GoldDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String details;

    @Column(nullable = true)
    private int noOfBags;

    @Column
    private BigDecimal grossWeightInGm;

    @Column
    private BigDecimal netWeightInGm;

    private BigDecimal rateGm;

    private BigDecimal netValue;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public int getNoOfBags() {
		return noOfBags;
	}

	public void setNoOfBags(int noOfBags) {
		this.noOfBags = noOfBags;
	}

	public BigDecimal getGrossWeightInGm() {
		return grossWeightInGm;
	}

	public void setGrossWeightInGm(BigDecimal grossWeightInGm) {
		this.grossWeightInGm = grossWeightInGm;
	}

	public BigDecimal getNetWeightInGm() {
		return netWeightInGm;
	}

	public void setNetWeightInGm(BigDecimal netWeightInGm) {
		this.netWeightInGm = netWeightInGm;
	}

	public BigDecimal getRateGm() {
		return rateGm;
	}

	public void setRateGm(BigDecimal rateGm) {
		this.rateGm = rateGm;
	}

	public BigDecimal getNetValue() {
		return netValue;
	}

	public void setNetValue(BigDecimal netValue) {
		this.netValue = netValue;
	}
    
    
    
    
    
}