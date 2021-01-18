package com.dfq.coeffi.cbs.master.entity.accounthead;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity(name = "account_head_ledger")
public class Ledger implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @OneToOne
    private AccountHead accountHead;

    private boolean active;

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

	public AccountHead getAccountHead() {
		return accountHead;
	}

	public void setAccountHead(AccountHead accountHead) {
		this.accountHead = accountHead;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    
    
}
