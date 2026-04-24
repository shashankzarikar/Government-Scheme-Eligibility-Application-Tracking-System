package com.government.scheme_management.dto;
import lombok.Data;

@Data
public class DashboardStats {
    private long totalUsers;
    private long totalApplications;
    private long activeSchemes;
    private long pendingApplications;
    private long approvedApplications;
    private long rejectedApplications;
}
