package com.tgdating.aggregation.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RequestProfileDeleteDto {
    @NotNull
    private String sessionId;
}
