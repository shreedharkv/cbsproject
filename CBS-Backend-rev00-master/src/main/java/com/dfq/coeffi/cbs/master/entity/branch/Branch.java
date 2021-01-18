package com.dfq.coeffi.cbs.master.entity.branch;

import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
public class Branch implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private boolean active;

    @Temporal(TemporalType.DATE)
    private Date activationDate;

    @OneToOne
    private FinancialYear currentFinancialYear;

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public FinancialYear getCurrentFinancialYear() {
		return currentFinancialYear;
	}

	public void setCurrentFinancialYear(FinancialYear currentFinancialYear) {
		this.currentFinancialYear = currentFinancialYear;
	}
    
    
    
    
    
    
    
    
    
    

}