package com.dfq.coeffi.cbs.deposit.Dto;

import com.dfq.coeffi.cbs.deposit.api.DepositNomineeDetails;
import com.dfq.coeffi.cbs.deposit.entity.DepositType;
import com.dfq.coeffi.cbs.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter
public class DepositNomineeDto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String relationWithMember;

    private String name;

    private String residentiaAddress;

    private String village;

    private String taluka;

    private String district;

    private String phoneNumber;

    private String age;

    private String pinCode;

    private Boolean minor;

    @OneToOne(cascade = CascadeType.ALL)
    private DepositNomineeDetails depositNomineeDetailsTwo;

    @OneToOne(cascade = CascadeType.ALL)
    private DepositNomineeDetails depositNomineeDetailsThree;
}
