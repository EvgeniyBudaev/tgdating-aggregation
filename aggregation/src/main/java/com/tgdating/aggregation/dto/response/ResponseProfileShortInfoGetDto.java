package com.tgdating.aggregation.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseProfileShortInfoGetDto {
    private String sessionId;
    private Boolean isDeleted;
    private Boolean isBlocked;
}
