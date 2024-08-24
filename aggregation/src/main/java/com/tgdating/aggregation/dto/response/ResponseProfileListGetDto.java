package com.tgdating.aggregation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class ResponseProfileListGetDto {
    private String sessionId;
    private Integer distance;
    private String url;
    private Boolean isOnline;
    private LocalDateTime lastOnline;
}
