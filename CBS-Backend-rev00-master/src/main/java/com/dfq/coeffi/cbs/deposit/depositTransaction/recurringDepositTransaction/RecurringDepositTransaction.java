package com.dfq.coeffi.cbs.deposit.depositTransaction.recurringDepositTransaction;

import com.dfq.coeffi.cbs.deposit.entity.RecurringDeposit;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@ToString
public class RecurringDepositTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String remark;

    @Temporal(TemporalType.DATE)
    private Date transactionOn;

    @CreationTimestamp
    private Date createdOn;

    private BigDecimal creditAmount;

    private BigDecimal debitAmount;

    private BigDecimal balance;

    private String transactionType;

    private String transferType;

    private String voucherType;

    private BigDecimal depositAmount;

    private BigDecimal totalPrincipleAmount;

    @OneToOne
    private User transactionBy;

    @OneToOne
    private RecurringDeposit recurringDeposit;

    private String accountNumber;

    private String name;
}
