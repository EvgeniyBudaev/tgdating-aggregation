package com.tgdating.aggregation.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestProfileNavigatorAddDto {
    private String sessionId;
    private String name;
    private Double latitude;
    private Double longitude;
}
