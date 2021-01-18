package com.dfq.coeffi.cbs.user.dto;

import com.dfq.coeffi.cbs.user.entity.ContactInformation;
import com.dfq.coeffi.cbs.user.entity.PersonnelDetails;
import com.dfq.coeffi.cbs.user.entity.UserAddress;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class UserDto {

    private long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String userCode;
    private Date dateOfJoining;
    private BigDecimal offeredSalary;
    private String gender;
    private String password;
    private String email;
    private String jobTitle;
    private boolean active;
    private long roleId;

    private UserAddress userAddress;
    private ContactInformation contactInformation;
    private PersonnelDetails personnelDetails;
}