package com.tgdating.aggregation.dto.request;

import lombok.Data;

@Data
public class RequestProfileNavigatorAddDto {
    private String sessionId;
    private String name;
    private Double latitude;
    private Double longitude;
}
