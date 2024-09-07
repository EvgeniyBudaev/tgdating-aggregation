package com.tgdating.aggregation.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestProfileBlockAddDto {
    private String sessionId; // id того кто блокирует
    private String blockedUserSessionId; // id того кого блокируем
}
