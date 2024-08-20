package com.tgdating.aggregation.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestProfileImageAddDto {
    private Long profileId;
    private String name;
    private String url;
    private Long size;
    private Boolean isDeleted;
    private Boolean isBlocked;
    private Boolean isPremium;
    private Boolean isPrivate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
