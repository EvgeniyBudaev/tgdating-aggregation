package com.tgdating.aggregation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
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
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
