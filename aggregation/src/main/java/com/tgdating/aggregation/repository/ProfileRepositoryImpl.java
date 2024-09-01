package com.tgdating.aggregation.repository;

import com.tgdating.aggregation.dto.request.*;
import com.tgdating.aggregation.model.*;
import com.tgdating.aggregation.repository.mapper.*;
import com.tgdating.aggregation.shared.utils.Utils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ProfileRepositoryImpl implements ProfileRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String CREATE_PROFILE =
            "INSERT INTO profiles (session_id, display_name, birthday, gender, location, description, height,\n"
                    + "weight, created_at, updated_at, last_online)\n"
                    + "VALUES (:sessionId, :displayName, :birthday, :gender, :location, :description, :height,\n"
                    + ":weight, :createdAt, :updatedAt, :lastOnline)";

    private static final String UPDATE_PROFILE =
            "UPDATE profiles\n"
                    + "SET display_name = :displayName, birthday = :birthday, gender = :gender,\n"
                    + "location = :location, description = :description, height = :height, weight = :weight,\n"
                    + "updated_at = :updatedAt, last_online = :lastOnline\n"
                    + "WHERE session_id = :sessionId";

    private static final String GET_PROFILE_LIST_BY_SESSION_ID =
            "SELECT p.id, p.session_id, p.display_name, p.birthday, p.gender, p.location, p.description, p.height,\n"
                    + "p.weight, p.is_deleted, p.is_blocked, p.is_premium, p.is_show_distance, p.is_invisible,\n"
                    + "p.created_at, p.updated_at, p.last_online,\n"
                    + "EXTRACT(YEAR FROM AGE(NOW(), p.birthday)) AS age,\n"
                    + "ST_Distance(\n"
                    + "(SELECT location FROM profile_navigators WHERE session_id = p.session_id)::geography,\n"
                    + "ST_SetSRID(ST_Force2D(ST_MakePoint(\n"
                    + "(SELECT ST_X(location) FROM profile_navigators WHERE session_id = :sessionId),\n"
                    + "(SELECT ST_Y(location) FROM profile_navigators WHERE session_id = :sessionId)\n"
                    + ")), 4326)::geography) AS distance\n"
                    + "FROM profiles p\n"
                    + "JOIN profile_navigators pn ON p.session_id = pn.session_id\n"
                    + "WHERE p.is_deleted = false AND  p.is_blocked = false AND\n"
                    + "(EXTRACT(YEAR FROM AGE(NOW(), p.birthday)) BETWEEN :ageFrom AND :ageTo) AND\n"
                    + "(:searchGender = 'all' OR gender = :searchGender) AND  p.session_id <> :sessionId AND\n"
                    + "NOT EXISTS (SELECT 1 FROM profile_blocks WHERE session_id = :sessionId AND blocked_user_session_id = p.session_id) AND\n"
                    + "ST_Distance((SELECT location FROM profile_navigators WHERE session_id = p.session_id)::geography,\n"
                    + "ST_SetSRID(ST_MakePoint((SELECT ST_X(location) FROM profile_navigators WHERE session_id = :sessionId),\n"
                    + "(SELECT ST_Y(location) FROM profile_navigators WHERE session_id = :sessionId)), 4326)::geography) <= :distanceMeters\n"
                    + "ORDER BY distance ASC, p.last_online DESC\n"
                    + "LIMIT :limit OFFSET :offset";

    private static final String GET_NUMBER_ENTITIES_BY_PROFILE_LIST =
            "SELECT COUNT(*)\n"
                    + "FROM profiles\n"
                    + "WHERE is_deleted=false AND is_blocked=false AND\n"
                    + "(EXTRACT(YEAR FROM AGE(NOW(), birthday)) BETWEEN :ageFrom AND :ageTo) AND\n"
                    + "(:searchGender = 'all' OR gender = :searchGender) AND session_id <> :sessionId\n";

    private static final String GET_PROFILE_BY_SESSION_ID =
            "SELECT id, session_id, display_name, birthday, gender, location, description, height,\n"
                    + "weight, is_deleted, is_blocked, is_premium, is_show_distance, is_invisible, created_at,\n"
                    + "updated_at, last_online\n"
                    + "FROM profiles\n"
                    + "WHERE session_id = :sessionId AND is_deleted = false";

    private static final String UPDATE_LAST_ONLINE =
            "UPDATE profiles SET last_online = :lastOnline WHERE session_id = :sessionId";

    private static final String ADD_IMAGE =
            "INSERT INTO profile_images (session_id, name, url, size, is_deleted, is_blocked, is_primary,\n"
                    + "is_private, created_at, updated_at)\n"
                    + "VALUES (:sessionId, :name, :url, :size, :isDeleted, :isBlocked, :isPrimary, :isPrivate,\n"
                    + ":createdAt, :updatedAt) RETURNING id";

    private static final String UPDATE_IMAGE =
            "UPDATE profile_images SET name = :name, url = :url, size = :size, is_deleted = :isDeleted,\n"
                    + "is_blocked = :isBlocked, is_primary = :isPrimary, is_private = :isPrivate,\n"
                    + "updated_at = :updatedAt WHERE id = :id";

    private static final String DELETE_IMAGE =
            "UPDATE profile_images SET is_deleted = :isDeleted, updated_at = :updatedAt WHERE id = :id";

    private static final String GET_IMAGE_LIST =
            "SELECT id, session_id, name, url, size, is_deleted, is_blocked, is_primary, is_private,\n"
                    + "created_at, updated_at\n"
                    + "FROM profile_images\n"
                    + "WHERE session_id = :sessionId AND is_deleted=false AND is_blocked=false";

    private static final String GET_IMAGE_PUBLIC_LIST =
            "SELECT id, session_id, name, url, size, is_deleted, is_blocked, is_primary, is_private,\n"
                    + "created_at, updated_at\n"
                    + "FROM profile_images\n"
                    + "WHERE session_id = :sessionId AND is_deleted=false AND is_blocked=false AND is_private=false";

    private static final String GET_IMAGE =
            "SELECT id, session_id, name, url, size, is_deleted, is_blocked, is_primary, is_private,\n"
                    + "created_at, updated_at\n"
                    + "FROM profile_images\n"
                    + "WHERE id = :id";


    private static final String ADD_NAVIGATOR =
            "INSERT INTO profile_navigators (session_id, location, created_at, updated_at)\n"
                    + "VALUES (:sessionId, ST_SetSRID(ST_MakePoint(:longitude, :latitude),  4326), :createdAt,\n"
                    + ":updatedAt) RETURNING id";

    private static final String UPDATE_NAVIGATOR =
            "UPDATE profile_navigators\n"
                    + "SET location = ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), updated_at=:updatedAt\n"
                    + "WHERE session_id = :sessionId";

    private static final String GET_NAVIGATOR =
            "SELECT id, session_id, ST_X(location) as longitude, ST_Y(location) as latitude, created_at, updated_at\n"
                    + "FROM profile_navigators WHERE session_id = :sessionId";

    private static final String ADD_FILTER =
            "INSERT INTO profile_filters (session_id, search_gender, looking_for, age_from, age_to, distance, page,\n"
                    + "size, created_at, updated_at)\n"
                    + "VALUES (:sessionId, :searchGender, :lookingFor, :ageFrom, :ageTo, :distance, :page, :size,\n"
                    + ":createdAt, :updatedAt)\n"
                    + "RETURNING id";

    private static final String UPDATE_FILTER =
            "UPDATE profile_filters\n"
                    + "SET session_id=:sessionId, search_gender=:searchGender, looking_for=:lookingFor,\n"
                    + "age_from=:ageFrom, age_to=:ageTo, distance=:distance, page=:page, size=:size,\n"
                    + "updated_at=:updatedAt\n"
                    + "WHERE session_id = :sessionId";

    private static final String GET_FILTER =
            "SELECT id, session_id, search_gender, looking_for, age_from, age_to, distance, page, size, created_at,\n"
                    + "updated_at\n"
                    + "FROM profile_filters\n"
                    + "WHERE session_id = :sessionId";

    private static final String ADD_TELEGRAM =
            "INSERT INTO profile_telegram (session_id, user_id, username, first_name, last_name, language_code,\n"
                    + "allows_write_to_pm, query_id, chat_id, is_deleted, created_at, updated_at)\n"
                    + "VALUES (:sessionId, :userId, :username, :firstName, :lastName, :languageCode,\n"
                    + ":allowsWriteToPm, :queryId, :chatId, :isDeleted, :createdAt, :updatedAt)\n"
                    + "RETURNING id";

    private static final String UPDATE_TELEGRAM =
            "UPDATE profile_telegram\n"
                    + "SET session_id=:sessionId, user_id=:userId, username=:username,\n"
                    + "first_name=:firstName, last_name=:lastName, language_code=:languageCode,\n"
                    + "allows_write_to_pm=:allowsWriteToPm, query_id=:queryId, chat_id=:chatId,\n"
                    + "is_deleted=:isDeleted, updated_at=:updatedAt\n"
                    + "WHERE session_id = :sessionId";

    private static final String DELETE_TELEGRAM =
            "UPDATE profile_telegram SET is_deleted = :isDeleted, updated_at = :updatedAt WHERE id = :id";

    private static final String GET_TELEGRAM =
            "SELECT id, session_id, user_id, username, first_name, last_name, language_code,\n"
                    + "allows_write_to_pm, query_id, chat_id, is_deleted, created_at, updated_at\n"
                    + "FROM profile_telegram\n"
                    + "WHERE session_id = :sessionId";

    private static final String ADD_LIKE =
            "INSERT INTO profile_likes (session_id, liked_session_id, is_liked, created_at, updated_at\n"
                    + "VALUES (:sessionId, :liked_session_id, :is_liked, :created_at, :updated_at)\n"
                    + "RETURNING id";

    private static final String GET_LIKE =
            "SELECT id, session_id, liked_session_id, is_liked, created_at, updated_at\n"
                    + "FROM profile_likes\n"
                    + "WHERE session_id = :sessionId AND liked_session_id = :likedSessionId";


    public ProfileRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional
    @Override
    public ProfileEntity create(RequestProfileCreateDto requestProfileCreateDto) {
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
                .addValue("createdAt", Utils.getNowUtc())
                .addValue("updatedAt", null)
                .addValue("lastOnline", Utils.getNowUtc());
        namedParameterJdbcTemplate.update(CREATE_PROFILE, parameters);
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM profiles WHERE session_id = :sessionId",
                parameters,
                new ProfileEntityRowMapper()
        );
    }

    @Override
    public ProfileEntity update(RequestProfileUpdateDto requestProfileUpdateDto) {
        Double height = requestProfileUpdateDto.getHeight() != null ? requestProfileUpdateDto.getHeight() : 0.0;
        Double weight = requestProfileUpdateDto.getWeight() != null ? requestProfileUpdateDto.getWeight() : 0.0;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", requestProfileUpdateDto.getSessionId())
                .addValue("displayName", requestProfileUpdateDto.getDisplayName())
                .addValue("birthday", requestProfileUpdateDto.getBirthday())
                .addValue("gender", requestProfileUpdateDto.getGender())
                .addValue("location", requestProfileUpdateDto.getLocation())
                .addValue("description", requestProfileUpdateDto.getDescription())
                .addValue("height", height)
                .addValue("weight", weight)
                .addValue("updatedAt", Utils.getNowUtc())
                .addValue("lastOnline", Utils.getNowUtc());
        namedParameterJdbcTemplate.update(UPDATE_PROFILE, parameters);
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM profiles WHERE session_id = :sessionId",
                parameters,
                new ProfileEntityRowMapper()
        );
    }

    @Override
    public PaginationEntity<List<ProfileListEntity>> findProfileList(
            RequestProfileListGetDto requestProfileListGetDto) {
        int ageFrom = requestProfileListGetDto.getAgeFrom();
        int ageTo = requestProfileListGetDto.getAgeTo();
        int page = requestProfileListGetDto.getPage();
        int size = requestProfileListGetDto.getSize();
        int offset = (page - 1) * size;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", requestProfileListGetDto.getSessionId())
                .addValue("searchGender", requestProfileListGetDto.getSearchGender())
                .addValue("ageFrom", ageFrom)
                .addValue("ageTo", ageTo)
                .addValue("distanceMeters", requestProfileListGetDto.getDistance() * 1000)
                .addValue("limit", size)
                .addValue("offset", offset);
        List<ProfileListEntity> profileList = namedParameterJdbcTemplate.query(
                GET_PROFILE_LIST_BY_SESSION_ID,
                parameters,
                new ProfileListEntityRowMapper()
        );
        Integer numberEntities = namedParameterJdbcTemplate.queryForObject(
                GET_NUMBER_ENTITIES_BY_PROFILE_LIST,
                parameters,
                Integer.class
        );
        PaginationEntity<List<ProfileListEntity>> paginationEntity =
                new PaginationEntity<>(page, size, numberEntities);
        paginationEntity.setContent(profileList);
        return paginationEntity;
    }

    @Override
    public ProfileEntity findBySessionID(String sessionId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", sessionId);
        return namedParameterJdbcTemplate.queryForObject(
                GET_PROFILE_BY_SESSION_ID,
                parameters,
                new ProfileEntityRowMapper()
        );
    }

    @Transactional
    @Override
    public void updateLastOnline(String sessionId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", sessionId)
                .addValue("lastOnline", Utils.getNowUtc());
        namedParameterJdbcTemplate.update(
                UPDATE_LAST_ONLINE,
                parameters
        );
    }

    @Transactional
    @Override
    public ProfileImageEntity addImage(RequestProfileImageAddDto requestProfileImageAddDto) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", requestProfileImageAddDto.getSessionId())
                .addValue("name", requestProfileImageAddDto.getName())
                .addValue("url", requestProfileImageAddDto.getUrl())
                .addValue("size", requestProfileImageAddDto.getSize())
                .addValue("isDeleted", requestProfileImageAddDto.getIsDeleted())
                .addValue("isBlocked", requestProfileImageAddDto.getIsBlocked())
                .addValue("isPrimary", requestProfileImageAddDto.getIsPrimary())
                .addValue("isPrivate", requestProfileImageAddDto.getIsPrivate())
                .addValue("createdAt", Utils.getNowUtc())
                .addValue("updatedAt", null);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(ADD_IMAGE, parameters, keyHolder);
        long insertedId = keyHolder.getKey().longValue();
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM profile_images WHERE id = " + insertedId,
                parameters,
                new ProfileImageEntityRowMapper()
        );
    }

    @Override
    public ProfileImageEntity updateImage(RequestProfileImageUpdateDto requestProfileImageUpdateDto) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", requestProfileImageUpdateDto.getId())
                .addValue("name", requestProfileImageUpdateDto.getName())
                .addValue("url", requestProfileImageUpdateDto.getUrl())
                .addValue("size", requestProfileImageUpdateDto.getSize())
                .addValue("isDeleted", requestProfileImageUpdateDto.getIsDeleted())
                .addValue("isBlocked", requestProfileImageUpdateDto.getIsBlocked())
                .addValue("isPrimary", requestProfileImageUpdateDto.getIsPrimary())
                .addValue("isPrivate", requestProfileImageUpdateDto.getIsPrivate())
                .addValue("updatedAt", Utils.getNowUtc());
        namedParameterJdbcTemplate.update(UPDATE_IMAGE, parameters);
        return namedParameterJdbcTemplate.queryForObject(
                GET_IMAGE,
                parameters,
                new ProfileImageEntityRowMapper()
        );
    }

    @Override
    public ProfileImageEntity deleteImage(Long id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("isDeleted", true)
                .addValue("updatedAt", Utils.getNowUtc());
        namedParameterJdbcTemplate.update(DELETE_IMAGE, parameters);
        return namedParameterJdbcTemplate.queryForObject(
                GET_IMAGE,
                parameters,
                new ProfileImageEntityRowMapper()
        );
    }

    @Override
    public List<ProfileImageEntity> findImageListBySessionID(String sessionId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", sessionId);
        return namedParameterJdbcTemplate.query(
                GET_IMAGE_LIST,
                parameters,
                new ProfileImageEntityRowMapper()
        );
    }

    @Override
    public List<ProfileImageEntity> findImagePublicListBySessionID(String sessionId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", sessionId);
        return namedParameterJdbcTemplate.query(
                GET_IMAGE_PUBLIC_LIST,
                parameters,
                new ProfileImageEntityRowMapper()
        );
    }

    @Override
    public ProfileImageEntity findImageByID(Long id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);
        return namedParameterJdbcTemplate.queryForObject(
                GET_IMAGE,
                parameters,
                new ProfileImageEntityRowMapper()
        );
    }

    @Transactional
    @Override
    public ProfileNavigatorEntity addNavigator(RequestProfileNavigatorAddDto requestProfileNavigatorAddDto) {
        String sessionId = requestProfileNavigatorAddDto.getSessionId();
        Double latitude = requestProfileNavigatorAddDto.getLatitude();
        Double longitude = requestProfileNavigatorAddDto.getLongitude();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", sessionId)
                .addValue("longitude", longitude) // долгота
                .addValue("latitude", latitude) // широта
                .addValue("createdAt", Utils.getNowUtc())
                .addValue("updatedAt", null);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(ADD_NAVIGATOR, parameters, keyHolder);
        long insertedId = keyHolder.getKey().longValue();
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT id, session_id, ST_X(location) as longitude, ST_Y(location) as latitude,\n"
                        + "created_at, updated_at\n"
                        + "FROM profile_navigators WHERE id = " + insertedId,
                parameters,
                new ProfileNavigatorEntityRowMapper()
        );
    }

    @Override
    public ProfileNavigatorEntity updateNavigator(RequestProfileNavigatorUpdateDto requestProfileNavigatorUpdateDto) {
        String sessionId = requestProfileNavigatorUpdateDto.getSessionId();
        Double latitude = requestProfileNavigatorUpdateDto.getLatitude();
        Double longitude = requestProfileNavigatorUpdateDto.getLongitude();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", sessionId)
                .addValue("longitude", longitude) // долгота
                .addValue("latitude", latitude) // широта
                .addValue("updatedAt", Utils.getNowUtc());
        namedParameterJdbcTemplate.update(
                UPDATE_NAVIGATOR,
                parameters
        );
        return namedParameterJdbcTemplate.queryForObject(
                GET_NAVIGATOR,
                parameters,
                new ProfileNavigatorEntityRowMapper()
        );
    }

    @Override
    public ProfileNavigatorEntity findNavigatorBySessionID(String sessionId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", sessionId);
        return namedParameterJdbcTemplate.queryForObject(
                GET_NAVIGATOR,
                parameters,
                new ProfileNavigatorEntityRowMapper()
        );
    }

    @Transactional
    @Override
    public ProfileFilterEntity addFilter(RequestProfileFilterAddDto requestProfileFilterAddDto) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", requestProfileFilterAddDto.getSessionId())
                .addValue("searchGender", requestProfileFilterAddDto.getSearchGender())
                .addValue("lookingFor", requestProfileFilterAddDto.getLookingFor())
                .addValue("ageFrom", requestProfileFilterAddDto.getAgeFrom())
                .addValue("ageTo", requestProfileFilterAddDto.getAgeTo())
                .addValue("distance", requestProfileFilterAddDto.getDistance())
                .addValue("page", requestProfileFilterAddDto.getPage())
                .addValue("size", requestProfileFilterAddDto.getSize())
                .addValue("createdAt", Utils.getNowUtc())
                .addValue("updatedAt", null);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(ADD_FILTER, parameters, keyHolder);
        long insertedId = keyHolder.getKey().longValue();
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM profile_filters WHERE id = " + insertedId,
                parameters,
                new ProfileFilterEntityRowMapper()
        );
    }

    @Override
    public ProfileFilterEntity updateFilter(RequestProfileFilterUpdateDto requestProfileFilterUpdateDto) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", requestProfileFilterUpdateDto.getSessionId())
                .addValue("searchGender", requestProfileFilterUpdateDto.getSearchGender())
                .addValue("lookingFor", requestProfileFilterUpdateDto.getLookingFor())
                .addValue("ageFrom", requestProfileFilterUpdateDto.getAgeFrom())
                .addValue("ageTo", requestProfileFilterUpdateDto.getAgeTo())
                .addValue("distance", requestProfileFilterUpdateDto.getDistance())
                .addValue("page", requestProfileFilterUpdateDto.getPage())
                .addValue("size", requestProfileFilterUpdateDto.getSize())
                .addValue("updatedAt", Utils.getNowUtc());
        namedParameterJdbcTemplate.update(UPDATE_FILTER, parameters);
        return namedParameterJdbcTemplate.queryForObject(
                GET_FILTER,
                parameters,
                new ProfileFilterEntityRowMapper()
        );
    }

    @Override
    public ProfileFilterEntity findFilterBySessionID(String sessionId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", sessionId);
        return namedParameterJdbcTemplate.queryForObject(
                GET_FILTER,
                parameters,
                new ProfileFilterEntityRowMapper()
        );
    }

    @Transactional
    @Override
    public ProfileTelegramEntity addTelegram(RequestProfileTelegramAddDto requestProfileTelegramAddDto) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", requestProfileTelegramAddDto.getSessionId())
                .addValue("userId", requestProfileTelegramAddDto.getUserId())
                .addValue("username", requestProfileTelegramAddDto.getUsername())
                .addValue("firstName", requestProfileTelegramAddDto.getFirstName())
                .addValue("lastName", requestProfileTelegramAddDto.getLastName())
                .addValue("languageCode", requestProfileTelegramAddDto.getLanguageCode())
                .addValue("allowsWriteToPm", requestProfileTelegramAddDto.getAllowsWriteToPm())
                .addValue("queryId", requestProfileTelegramAddDto.getQueryId())
                .addValue("chatId", requestProfileTelegramAddDto.getChatId())
                .addValue("isDeleted", false)
                .addValue("createdAt", Utils.getNowUtc())
                .addValue("updatedAt", null);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(ADD_TELEGRAM, parameters, keyHolder);
        long insertedId = keyHolder.getKey().longValue();
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM profile_telegram WHERE id = " + insertedId,
                parameters,
                new ProfileTelegramEntityRowMapper()
        );
    }

    @Transactional
    @Override
    public ProfileTelegramEntity updateTelegram(RequestProfileTelegramUpdateDto requestProfileTelegramUpdateDto) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", requestProfileTelegramUpdateDto.getSessionId())
                .addValue("userId", requestProfileTelegramUpdateDto.getUserId())
                .addValue("username", requestProfileTelegramUpdateDto.getUsername())
                .addValue("firstName", requestProfileTelegramUpdateDto.getFirstName())
                .addValue("lastName", requestProfileTelegramUpdateDto.getLastName())
                .addValue("languageCode", requestProfileTelegramUpdateDto.getLanguageCode())
                .addValue("allowsWriteToPm", requestProfileTelegramUpdateDto.getAllowsWriteToPm())
                .addValue("queryId", requestProfileTelegramUpdateDto.getQueryId())
                .addValue("chatId", requestProfileTelegramUpdateDto.getChatId())
                .addValue("isDeleted", false)
                .addValue("updatedAt", Utils.getNowUtc());
        namedParameterJdbcTemplate.update(UPDATE_TELEGRAM, parameters);
        return namedParameterJdbcTemplate.queryForObject(
                GET_TELEGRAM,
                parameters,
                new ProfileTelegramEntityRowMapper()
        );
    }

    @Override
    public ProfileTelegramEntity deleteTelegram(Long id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("isDeleted", true)
                .addValue("updatedAt", Utils.getNowUtc());
        namedParameterJdbcTemplate.update(DELETE_TELEGRAM, parameters);
        return namedParameterJdbcTemplate.queryForObject(
                GET_TELEGRAM,
                parameters,
                new ProfileTelegramEntityRowMapper()
        );
    }

    @Override
    public ProfileTelegramEntity findTelegramBySessionID(String sessionId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", sessionId);
        return namedParameterJdbcTemplate.queryForObject(
                GET_TELEGRAM,
                parameters,
                new ProfileTelegramEntityRowMapper()
        );
    }

    @Override
    public ProfileLikeEntity addLike(RequestProfileLikeAddDto requestProfileLikeAddDto) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sessionId", requestProfileLikeAddDto.getSessionId())
                .addValue("likedSessionId", requestProfileLikeAddDto.getLikedSessionId())
                .addValue("isLiked", true)
                .addValue("createdAt", Utils.getNowUtc())
                .addValue("updatedAt", null);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(ADD_LIKE, parameters, keyHolder);
        long insertedId = keyHolder.getKey().longValue();
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM profile_likes WHERE id = " + insertedId,
                parameters,
                new ProfileLikeEntityRowMapper()
        );
    }

    @Override
    public ProfileLikeEntity findLikeBySessionID(String sessionId, String likedSessionId) {
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("sessionId", sessionId)
                    .addValue("likedSessionId", likedSessionId);
            return namedParameterJdbcTemplate.queryForObject(
                    GET_LIKE,
                    parameters,
                    new ProfileLikeEntityRowMapper());

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
