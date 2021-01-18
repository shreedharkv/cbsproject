package com.dfq.coeffi.cbs.common.address;

import com.dfq.coeffi.cbs.loan.entity.Guarantor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@ToString
@Entity
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    private String area;
    private String village;
    private String taluka;
    private String district;
    private String pinCode;
    private String telephone;

}