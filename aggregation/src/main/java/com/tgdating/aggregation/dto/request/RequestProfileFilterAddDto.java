package com.tgdating.aggregation.dto.request;

import lombok.Data;

@Data
public class RequestProfileFilterAddDto {
    private String sessionId;
    private String searchGender;
    private String lookingFor;
    private Byte ageFrom;
    private Byte ageTo;
    private Double distance;
    private Integer page;
    private Integer size;
}
