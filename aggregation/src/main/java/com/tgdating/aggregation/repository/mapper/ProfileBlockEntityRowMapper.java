package com.tgdating.aggregation.repository.mapper;

import com.tgdating.aggregation.model.ProfileBlockEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class ProfileBlockEntityRowMapper implements RowMapper<ProfileBlockEntity> {
    @Override
    public ProfileBlockEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProfileBlockEntity(
                rs.getLong("id"),
                rs.getString("session_id"),
                rs.getString("blocked_user_session_id"),
                rs.getBoolean("is_blocked"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                Optional.ofNullable(rs.getTimestamp("updated_at"))
                        .map(Timestamp::toLocalDateTime).orElse(null)
        );
    }
}
