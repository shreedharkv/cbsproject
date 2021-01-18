package com.dfq.coeffi.cbs.deposit.depositTransaction.currentAccountTransaction;

import com.dfq.coeffi.cbs.deposit.entity.CurrentAccount;
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
public class CurrentAccountTransaction {
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

    private String voucherType;

    @OneToOne
    private User transactionBy;

    @OneToOne
    private CurrentAccount currentAccount;

    private String accountNumber;

    private String name;
}
