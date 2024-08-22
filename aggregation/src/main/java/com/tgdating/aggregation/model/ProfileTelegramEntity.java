package com.tgdating.aggregation.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileTelegramEntity {
    private Long id;
    private String sessionId;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String languageCode;
    private Boolean allowsWriteToPm;
    private String queryId;
    private Long chatId;
}
