package com.dfq.coeffi.cbs.member.entity;

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
public class ShareMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private BigDecimal shareValue;

    private String name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getShareValue() {
		return shareValue;
	}

	public void setShareValue(BigDecimal shareValue) {
		this.shareValue = shareValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    
    
    
    
    
    
}