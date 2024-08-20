package com.tgdating.aggregation.repository;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.dto.request.RequestProfileImageAddDto;
import com.tgdating.aggregation.model.ProfileEntity;
import com.tgdating.aggregation.model.ProfileImageEntity;
import com.tgdating.aggregation.shared.exception.InternalServerException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class ProfileRepositoryImpl implements ProfileRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String CREATE_PROFILE =
            "INSERT INTO profiles (session_id, display_name, birthday, gender, location, description," +
                    " height, weight, is_deleted, is_blocked, is_premium, is_show_distance, is_invisible," +
                    " created_at, updated_at, last_online)" +
                    " VALUES (:sessionId, :displayName, :birthday, :gender, :location, :description, :height, :weight," +
                    " :isDeleted, :isBlocked, :isPremium, :isShowDistance, :isInvisible, :createdAt, :updatedAt," +
                    " :lastOnline)";

    private static final String ADD_PROFILE_IMAGE =
            "INSERT INTO profile_images (profile_id, name, url, size, is_deleted, is_blocked, is_premium, is_private," +
                    " created_at, updated_at)" +
                    " VALUES (:profileId, :name, :url, :size, :isDeleted, :isBlocked, :isPremium, :isPrivate," +
                    " :createdAt, :updatedAt)";

    public ProfileRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
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
                    .addValue("isShowDistance", true)
                    .addValue("isInvisible", false)
                    .addValue("createdAt", LocalDateTime.now())
                    .addValue("updatedAt", null)
                    .addValue("lastOnline", LocalDateTime.now());
            namedParameterJdbcTemplate.update(CREATE_PROFILE, parameters);
            return namedParameterJdbcTemplate.queryForObject(
                    "SELECT * FROM profiles WHERE session_id = :sessionId",
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
                            .updatedAt(null)
                            .lastOnline(resultSet.getTimestamp("last_online").toLocalDateTime())
                            .build()
            );
        } catch (Exception e) {
            throw new InternalServerException(
                    "Ошибка сервера",
                    e.getMessage()
            );
        }
    }

    @Override
    public ProfileImageEntity addImage(RequestProfileImageAddDto requestProfileImageAddDto) {
        try {
            System.out.println("requestProfileImageAddDto: " + requestProfileImageAddDto);
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("profileId", requestProfileImageAddDto.getProfileId())
                    .addValue("name", requestProfileImageAddDto.getName())
                    .addValue("url", requestProfileImageAddDto.getUrl())
                    .addValue("size", requestProfileImageAddDto.getSize())
                    .addValue("isDeleted", requestProfileImageAddDto.getIsDeleted())
                    .addValue("isBlocked", requestProfileImageAddDto.getIsBlocked())
                    .addValue("isPremium", requestProfileImageAddDto.getIsPremium())
                    .addValue("isPrivate", requestProfileImageAddDto.getIsPrivate())
                    .addValue("createdAt", requestProfileImageAddDto.getCreatedAt())
                    .addValue("updatedAt", requestProfileImageAddDto.getUpdatedAt());
            System.out.println("parameters: " + parameters);
            namedParameterJdbcTemplate.update(ADD_PROFILE_IMAGE, parameters);
            System.out.println("UPDATED");
            return namedParameterJdbcTemplate.queryForObject(
                    "SELECT * FROM profile_images WHERE profile_id = :profileId",
                    parameters,
                    (resultSet, i) -> ProfileImageEntity.builder()
                            .id(resultSet.getLong("id"))
                            .profileId(resultSet.getLong("profile_id"))
                            .name(resultSet.getString("name"))
                            .url(resultSet.getString("url"))
                            .size(resultSet.getLong("size"))
                            .isDeleted(resultSet.getBoolean("is_deleted"))
                            .isBlocked(resultSet.getBoolean("is_blocked"))
                            .isPremium(resultSet.getBoolean("is_premium"))
                            .isPrivate(resultSet.getBoolean("is_private"))
                            .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(null)
                            .build()
            );
        } catch (Exception e) {
            throw new InternalServerException("Ошибка сервера", e.getMessage()
            );
        }
    }
}
