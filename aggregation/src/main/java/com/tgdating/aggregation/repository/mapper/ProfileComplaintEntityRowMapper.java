package com.tgdating.aggregation.repository.mapper;

import com.tgdating.aggregation.model.ProfileComplaintEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class ProfileComplaintEntityRowMapper implements RowMapper<ProfileComplaintEntity> {
    @Override
    public ProfileComplaintEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProfileComplaintEntity(
                rs.getLong("id"),
                rs.getString("session_id"),
                rs.getString("criminal_session_id"),
                rs.getString("reason"),
                rs.getBoolean("is_deleted"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                Optional.ofNullable(rs.getTimestamp("updated_at"))
                        .map(Timestamp::toLocalDateTime).orElse(null)
        );
    }
}
