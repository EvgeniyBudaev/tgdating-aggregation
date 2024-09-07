package com.tgdating.aggregation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class ProfileComplaintEntity {
    private Long id;
    private String sessionId;
    private String criminalSessionId;
    private String reason;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
