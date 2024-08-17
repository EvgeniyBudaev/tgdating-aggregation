package com.tgdating.aggregation.repository;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.model.ProfileEntity;
import com.tgdating.aggregation.shared.exception.InternalServerException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class ProfileImpl implements ProfileRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//    private static final String CREATE_PROFILE =
//            "INSERT INTO profiles (session_id, display_name, birthday, gender, location, description," +
//                    " height, weight, is_deleted, is_blocked, is_premium, is_show_distance, is_invisible," +
//                    " created_at, updated_at, last_online)" +
//                    "  VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16) RETURNING id";
private static final String CREATE_PROFILE =
        "INSERT INTO profiles (session_id, display_name, birthday, gender, location, description," +
                " height, weight, is_deleted, is_blocked, is_premium, is_show_distance, is_invisible," +
                " created_at, updated_at, last_online)" +
                " VALUES (:session_id, :display_name, :birthday, :gender, :location, :description, :height, :weight, :is_deleted, :is_blocked, :is_premium, :is_show_distance, :is_invisible, :created_at, :updated_at, :last_online)";

    public ProfileImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional
    @Override
    public ProfileEntity create(RequestProfileCreateDto requestProfileCreateDto) {
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("sessionId", requestProfileCreateDto.getSessionId())
                    .addValue("displayName", requestProfileCreateDto.getDisplayName())
                    .addValue("birthday", requestProfileCreateDto.getBirthday())
                    .addValue("gender", requestProfileCreateDto.getGender())
                    .addValue("location", requestProfileCreateDto.getLocation())
                    .addValue("description", requestProfileCreateDto.getDescription())
                    .addValue("height", requestProfileCreateDto.getHeight())
                    .addValue("weight", requestProfileCreateDto.getWeight())
                    .addValue("isDeleted", false)
                    .addValue("isBlocked", false)
                    .addValue("isPremium", false)
                    .addValue("isShowDistance", false)
                    .addValue("isInvisible", false)
                    .addValue("createdAt", LocalDateTime.now())
                    .addValue("updatedAt", LocalDateTime.now())
                    .addValue("lastOnline", LocalDateTime.now());
            namedParameterJdbcTemplate.update(CREATE_PROFILE, parameters);
            return namedParameterJdbcTemplate.queryForObject(
                    "SELECT * FROM profiles WHERE location = :location",
                    parameters,
                    (resultSet, i) -> ProfileEntity.builder()
                            .id(resultSet.getLong("id"))
                            .sessionId(resultSet.getString("session_id"))
                            .displayName(resultSet.getString("display_name"))
                            .birthday(resultSet.getTimestamp("birthday").toLocalDateTime().toLocalDate())
                            .gender(resultSet.getString("gender"))
                            .location(resultSet.getString("location"))
                            .description(resultSet.getString("description"))
                            .height(resultSet.getDouble("height"))
                            .weight(resultSet.getDouble("weight"))
                            .isDeleted(resultSet.getBoolean("is_deleted"))
                            .isBlocked(resultSet.getBoolean("is_blocked"))
                            .isPremium(resultSet.getBoolean("is_premium"))
                            .isShowDistance(resultSet.getBoolean("is_show_distance"))
                            .isInvisible(resultSet.getBoolean("is_invisible"))
                            .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime())
                            .lastOnline(resultSet.getTimestamp("last_online").toLocalDateTime())
                            .build()
            );
        } catch (Exception e) {
            throw new InternalServerException(
                    "Ошибка сервера",
                    "Профиль с name: " + requestProfileCreateDto.getDisplayName() +
                            " не удалось создать. Ошибка: " + e.getMessage()
            );
        }
    }
}
