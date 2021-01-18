package com.dfq.coeffi.cbs.deposit.api;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Setter
@Getter
@Entity
public class DepositNomineeDetails {

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
}