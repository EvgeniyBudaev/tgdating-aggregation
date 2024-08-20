package com.tgdating.aggregation.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestProfileImageAddDto {
    private String sessionId;
    private String name;
    private String url;
    private Long size;
    private Boolean isDeleted;
    private Boolean isBlocked;
    private Boolean isPrimary;
    private Boolean isPrivate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
