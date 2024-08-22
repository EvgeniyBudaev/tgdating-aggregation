package com.tgdating.aggregation.repository.mapper;

import com.tgdating.aggregation.model.ProfileImageEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class ProfileImageEntityRowMapper implements RowMapper<ProfileImageEntity> {
    @Override
    public ProfileImageEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProfileImageEntity(
                rs.getLong("id"),
                rs.getString("session_id"),
                rs.getString("name"),
                rs.getString("url"),
                rs.getLong("size"),
                rs.getBoolean("is_deleted"),
                rs.getBoolean("is_blocked"),
                rs.getBoolean("is_primary"),
                rs.getBoolean("is_private"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                Optional.ofNullable(rs.getTimestamp("updated_at"))
                        .map(Timestamp::toLocalDateTime).orElse(null)
        );
    }
}
