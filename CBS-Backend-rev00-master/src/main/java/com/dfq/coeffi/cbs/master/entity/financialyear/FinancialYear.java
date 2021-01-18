package com.dfq.coeffi.cbs.master.entity.financialyear;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
public class FinancialYear implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "financial_year_range")
    private String range;

    private String runningYear;

    private boolean active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getRunningYear() {
		return runningYear;
	}

	public void setRunningYear(String runningYear) {
		this.runningYear = runningYear;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    
    
    
    
}