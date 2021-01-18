package com.dfq.coeffi.cbs.user.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordDto {

        private String confirmPassword;
        private String currentPassword;
        private String newPassword;
        private long userId;
}