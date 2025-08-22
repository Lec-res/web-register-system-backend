package com.example.loginsystem.dto;

import com.example.loginsystem.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 注册请求DTO
 */
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    @NotNull(message = "用户角色不能为空")
    private UserRole role;

    // 默认构造函数
    public RegisterRequest() {
        this.role = UserRole.USER; // 默认为普通用户
    }

    // 带参构造函数
    public RegisterRequest(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role != null ? role : UserRole.USER;
    }

    // Getter和Setter方法
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", role=" + role +
                '}';
    }
}