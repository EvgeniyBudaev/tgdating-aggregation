package com.tgdating.aggregation.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestProfileImageAddDto {
    private String sessionId;
    private String name;
    private String url;
    private Long size;
    private Boolean isDeleted;
    private Boolean isBlocked;
    private Boolean isPrimary;
    private Boolean isPrivate;
}
