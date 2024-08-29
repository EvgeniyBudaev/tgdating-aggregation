package com.tgdating.aggregation.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class RequestProfileUpdateDto {
    @NotNull
    private String sessionId;
    @NotNull
    private String displayName;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime birthday;
    @NotNull
    private String gender;
    @NotNull
    private String searchGender;
    private String location;
    private String description;
    private Double height;
    private Double weight;
    private String lookingFor;
    @NotNull
    private Long telegramUserId;
    @NotNull
    private String telegramUsername;
    private String telegramFirstName;
    private String telegramLastName;
    private String telegramLanguageCode;
    private Boolean telegramAllowsWriteToPm;
    private String telegramQueryId;
    @NotNull
    private Long telegramChatId;
    private Double latitude;
    private Double longitude;
    private Integer ageFrom;
    private Integer ageTo;
    private Double distance;
    private Integer page;
    private Integer size;
    private MultipartFile[] image;
}
