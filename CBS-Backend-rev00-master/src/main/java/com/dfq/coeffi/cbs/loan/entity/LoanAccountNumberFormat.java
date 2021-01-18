package com.dfq.coeffi.cbs.loan.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Setter
@Getter
public class LoanAccountNumberFormat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long startingNumber;

    private long applicationStartNumber;

    private String prefix;

    private boolean active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getStartingNumber() {
		return startingNumber;
	}

	public void setStartingNumber(long startingNumber) {
		this.startingNumber = startingNumber;
	}

	public long getApplicationStartNumber() {
		return applicationStartNumber;
	}

	public void setApplicationStartNumber(long applicationStartNumber) {
		this.applicationStartNumber = applicationStartNumber;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    
    
    
    
    
    
}