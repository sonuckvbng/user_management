package com.usermanagement.binding;

import lombok.Data;

@Data
public class ActivateAccount {
    private String email;
    private String tempPassword;
    private String newPassword;
    private String confirmPassword;
}
