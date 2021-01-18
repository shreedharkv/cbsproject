package com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction;

import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
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
public class SavingBankTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String remark;

    private String accountNumber;

    private String name;

    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    private Date transactionOn;

    @CreationTimestamp
    private Date createdOn;

    private BigDecimal creditAmount;

    private BigDecimal debitAmount;

    private BigDecimal balance;

    private String transactionType;

    private String voucherType;

    private String transferType;

    private String chequeNumber;

    private Date chequeDate;

    @OneToOne
    private User transactionBy;

    @OneToOne
    private SavingsBankDeposit savingsBankDeposit;
}
