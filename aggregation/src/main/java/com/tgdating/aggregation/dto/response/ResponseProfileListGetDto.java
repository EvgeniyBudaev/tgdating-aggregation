package com.tgdating.aggregation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ResponseProfileListGetDto {
    private String sessionId;
}
