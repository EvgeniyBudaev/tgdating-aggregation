package com.tgdating.aggregation.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class RequestProfileCreateDto {
    @NotNull
    private String sessionId;
    @NotNull
    private String displayName;
    private LocalDate birthday;
    private String gender;
    private String location;
    private String description;
    private double height;
    private double weight;
}
