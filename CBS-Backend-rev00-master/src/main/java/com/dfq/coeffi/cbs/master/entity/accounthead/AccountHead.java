package com.dfq.coeffi.cbs.master.entity.accounthead;

import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity(name = "account_heads")
@ToString
public class AccountHead implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @OneToOne
    private Branch branch;

    @Enumerated(EnumType.STRING)
    private AccountHeadType accountHeadType;

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

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public AccountHeadType getAccountHeadType() {
		return accountHeadType;
	}

	public void setAccountHeadType(AccountHeadType accountHeadType) {
		this.accountHeadType = accountHeadType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    
    
    
    
    
    
    
    
    
    
}