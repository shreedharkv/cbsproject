package com.dfq.coeffi.cbs.master.entity.accountformat;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Setter
@Getter
@Entity
public class AccountFormat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long fromAccountNumber;

    private long toAccountNumber;

    @Enumerated(EnumType.STRING)
    private AccountFormatType accountFormatType;

    private String subType;

    private String prefix;

    private boolean active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFromAccountNumber() {
		return fromAccountNumber;
	}

	public void setFromAccountNumber(long fromAccountNumber) {
		this.fromAccountNumber = fromAccountNumber;
	}

	public long getToAccountNumber() {
		return toAccountNumber;
	}

	public void setToAccountNumber(long toAccountNumber) {
		this.toAccountNumber = toAccountNumber;
	}

	public AccountFormatType getAccountFormatType() {
		return accountFormatType;
	}

	public void setAccountFormatType(AccountFormatType accountFormatType) {
		this.accountFormatType = accountFormatType;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
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