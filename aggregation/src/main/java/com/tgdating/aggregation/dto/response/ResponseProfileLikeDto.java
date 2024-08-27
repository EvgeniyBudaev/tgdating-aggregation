package com.tgdating.aggregation.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResponseProfileLikeDto {
    private Long id;
    private String sessionId;
    private String likedSessionId;
    private Boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
