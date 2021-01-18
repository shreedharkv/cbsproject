package com.dfq.coeffi.cbs.loan.entity.loan;

import com.dfq.coeffi.cbs.deposit.entity.DepositType;
import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.deposit.entity.RecurringDeposit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

/**
 * Model class which contains deposit loan information
 * against which deposit customer/member took the loan
 *
 * @see com.dfq.coeffi.cbs.loan.entity.loan.LADDetails
 * @author Kapil Kumar
 * @since Feb-2019
 * @version 1.0
 */

@Entity
@Setter
@Getter
public class LADDetails implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    private long depositId;

    private DepositType depositType;

    @OneToOne
    private FixedDeposit fixedDeposit;

    @OneToOne
    private RecurringDeposit recurringDeposit;

    @OneToOne
    private PigmyDeposit pigmyDeposit;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDepositId() {
		return depositId;
	}

	public void setDepositId(long depositId) {
		this.depositId = depositId;
	}

	public DepositType getDepositType() {
		return depositType;
	}

	public void setDepositType(DepositType depositType) {
		this.depositType = depositType;
	}

	public FixedDeposit getFixedDeposit() {
		return fixedDeposit;
	}

	public void setFixedDeposit(FixedDeposit fixedDeposit) {
		this.fixedDeposit = fixedDeposit;
	}

	public RecurringDeposit getRecurringDeposit() {
		return recurringDeposit;
	}

	public void setRecurringDeposit(RecurringDeposit recurringDeposit) {
		this.recurringDeposit = recurringDeposit;
	}

	public PigmyDeposit getPigmyDeposit() {
		return pigmyDeposit;
	}

	public void setPigmyDeposit(PigmyDeposit pigmyDeposit) {
		this.pigmyDeposit = pigmyDeposit;
	}
    
    
    
    
    
    
    
    
    
    
    
    
}