package com.government.scheme_management.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RemarkResponse {
    private Integer remarkId;
    private String remark;
    private String officerName;
    private LocalDateTime addedAt;
}
