package com.tgdating.aggregation.repository.mapper;

import com.tgdating.aggregation.model.ProfileLikeEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class ProfileLikeEntityRowMapper implements RowMapper<ProfileLikeEntity> {
    @Override
    public ProfileLikeEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProfileLikeEntity(
                rs.getLong("id"),
                rs.getString("session_id"),
                rs.getString("liked_session_id"),
                rs.getBoolean("is_liked"),
                rs.getBoolean("is_deleted"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                Optional.ofNullable(rs.getTimestamp("updated_at"))
                        .map(Timestamp::toLocalDateTime).orElse(null)
        );
    }
}
