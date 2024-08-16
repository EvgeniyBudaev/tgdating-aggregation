package com.tgdating.aggregation.model;

import lombok.Data;

@Data
public class ErrorEntity {
    private String serviceName;
    private Integer statusCode;
    private Boolean success;
    private String prodMessage;
    private String devMessage;
}
