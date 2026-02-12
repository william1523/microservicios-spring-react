package com.will1523.login.dto;

import com.will1523.login.model.Role;

public class UserRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
    private String companyCode;

    public UserRequest() {}

    public UserRequest(String username, String email, String password, Role role, String companyCode) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.companyCode = companyCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
