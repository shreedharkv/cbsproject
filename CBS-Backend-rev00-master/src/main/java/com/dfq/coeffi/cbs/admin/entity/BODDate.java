package com.dfq.coeffi.cbs.admin.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
public class BODDate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.DATE)
    private Date bodDate;

    @Temporal(TemporalType.DATE)
    private Date eodDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date forDay;

    private Boolean bodStatus;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getBodDate() {
		return bodDate;
	}

	public void setBodDate(Date bodDate) {
		this.bodDate = bodDate;
	}

	public Date getEodDate() {
		return eodDate;
	}

	public void setEodDate(Date eodDate) {
		this.eodDate = eodDate;
	}

	public Date getForDay() {
		return forDay;
	}

	public void setForDay(Date forDay) {
		this.forDay = forDay;
	}

	public Boolean getBodStatus() {
		return bodStatus;
	}

	public void setBodStatus(Boolean bodStatus) {
		this.bodStatus = bodStatus;
	}
    
    
    
}