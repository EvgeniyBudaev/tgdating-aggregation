package com.tgdating.aggregation.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestProfileBlockAddDto {
    private String sessionId;
    private String blockedUserSessionId;
}
