package com.government.scheme_management.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Integer userId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
