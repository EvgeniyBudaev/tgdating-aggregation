package com.tgdating.aggregation.repository;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.dto.request.RequestProfileImageAddDto;
import com.tgdating.aggregation.dto.request.RequestProfileNavigatorAddDto;
import com.tgdating.aggregation.model.PointEntity;
import com.tgdating.aggregation.model.ProfileEntity;
import com.tgdating.aggregation.model.ProfileImageEntity;
import com.tgdating.aggregation.model.ProfileNavigatorEntity;
import com.tgdating.aggregation.shared.exception.InternalServerException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ProfileRepositoryImpl implements ProfileRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String CREATE_PROFILE =
            "INSERT INTO profiles (session_id, display_name, birthday, gender, location, description, height," +
                    " weight, created_at," +
                    " last_online)" +
                    " VALUES (:sessionId, :displayName, :birthday, :gender, :location, :description, :height," +
                    "  :weight, :createdAt," +
                    "  :lastOnline)";

    private static final String ADD_PROFILE_IMAGE =
            "INSERT INTO profile_images (session_id, name, url, size, is_deleted, is_blocked, is_primary, is_private," +
                    " created_at, updated_at)" +
                    " VALUES (:sessionId, :name, :url, :size, :isDeleted, :isBlocked, :isPrimary, :isPrivate," +
                    " :createdAt, :updatedAt) RETURNING id";

    private static final String ADD_PROFILE_NAVIGATOR =
            "INSERT INTO profile_navigators (session_id, location)" +
                    " VALUES (:sessionId, ST_SetSRID(ST_MakePoint(:latitude, :longitude),  4326)) RETURNING id";

    public ProfileRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional
    @Override
    public ProfileEntity create(RequestProfileCreateDto requestProfileCreateDto) {
        // TODO: разобраться с временем UTC
//        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC); // Получаем текущее время в формате UTC
//        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
//        String formattedTime = nowUtc.format(f);
//        System.out.println("TIME_3: " + formattedTime);
        try {
            Double height = requestProfileCreateDto.getHeight() != null ? requestProfileCreateDto.getHeight() : 0.0;
            Double weight = requestProfileCreateDto.getWeight() != null ? requestProfileCreateDto.getWeight() : 0.0;
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("sessionId", requestProfileCreateDto.getSessionId())
                    .addValue("displayName", requestProfileCreateDto.getDisplayName())
                    .addValue("birthday", requestProfileCreateDto.getBirthday())
                    .addValue("gender", requestProfileCreateDto.getGender())
                    .addValue("location", requestProfileCreateDto.getLocation())
                    .addValue("description", requestProfileCreateDto.getDescription())
                    .addValue("height", height)
                    .addValue("weight", weight)
                    .addValue("createdAt", LocalDateTime.now())
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

    @Transactional
    @Override
    public ProfileImageEntity addImage(RequestProfileImageAddDto requestProfileImageAddDto) {
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("sessionId", requestProfileImageAddDto.getSessionId())
                    .addValue("name", requestProfileImageAddDto.getName())
                    .addValue("url", requestProfileImageAddDto.getUrl())
                    .addValue("size", requestProfileImageAddDto.getSize())
                    .addValue("isDeleted", requestProfileImageAddDto.getIsDeleted())
                    .addValue("isBlocked", requestProfileImageAddDto.getIsBlocked())
                    .addValue("isPrimary", requestProfileImageAddDto.getIsPrimary())
                    .addValue("isPrivate", requestProfileImageAddDto.getIsPrivate())
                    .addValue("createdAt", requestProfileImageAddDto.getCreatedAt())
                    .addValue("updatedAt", requestProfileImageAddDto.getUpdatedAt());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(ADD_PROFILE_IMAGE, parameters, keyHolder);
            long insertedId = keyHolder.getKey().longValue();
            return namedParameterJdbcTemplate.queryForObject(
                    "SELECT * FROM profile_images WHERE id = " + insertedId,
                    parameters,
                    (resultSet, i) -> ProfileImageEntity.builder()
                            .id(resultSet.getLong("id"))
                            .sessionId(resultSet.getString("session_id"))
                            .name(resultSet.getString("name"))
                            .url(resultSet.getString("url"))
                            .size(resultSet.getLong("size"))
                            .isDeleted(resultSet.getBoolean("is_deleted"))
                            .isBlocked(resultSet.getBoolean("is_blocked"))
                            .isPrimary(resultSet.getBoolean("is_primary"))
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

    @Override
    public ProfileNavigatorEntity addNavigator(RequestProfileNavigatorAddDto requestProfileNavigatorAddDto) {
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("sessionId", requestProfileNavigatorAddDto.getSessionId())
                    .addValue("latitude", requestProfileNavigatorAddDto.getLatitude())
                    .addValue("longitude", requestProfileNavigatorAddDto.getLongitude());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(ADD_PROFILE_NAVIGATOR, parameters, keyHolder);
            long insertedId = keyHolder.getKey().longValue();
            return namedParameterJdbcTemplate.queryForObject(
                    "SELECT id, session_id, ST_X(location) as longitude, ST_Y(location) as latitude" +
                            " FROM profile_navigators WHERE id = " + insertedId,
                    parameters,
                    (resultSet, i) -> ProfileNavigatorEntity.builder()
                            .id(resultSet.getLong("id"))
                            .sessionId(resultSet.getString("session_id"))
                            .location(PointEntity.builder()
                                    .latitude(resultSet.getDouble("latitude"))
                                    .longitude(resultSet.getDouble("longitude"))
                                    .build())
                            .build()
            );
        } catch (Exception e) {
            throw new InternalServerException("Ошибка сервера", e.getMessage()
            );
        }
    }
}
