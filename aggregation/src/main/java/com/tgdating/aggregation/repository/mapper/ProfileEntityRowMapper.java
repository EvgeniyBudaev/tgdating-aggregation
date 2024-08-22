package com.tgdating.aggregation.repository.mapper;

import com.tgdating.aggregation.model.ProfileEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

public class ProfileEntityRowMapper implements RowMapper<ProfileEntity> {
    @Override
    public ProfileEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProfileEntity(
                rs.getLong("id"),
                rs.getString("session_id"),
                rs.getString("display_name"),
                Optional.ofNullable(rs.getTimestamp("birthday"))
                        .map(Timestamp::toLocalDateTime).map(LocalDate::from).orElse(null),
                rs.getString("gender"),
                rs.getString("location"),
                rs.getString("description"),
                rs.getDouble("height"),
                rs.getDouble("weight"),
                rs.getBoolean("is_deleted"),
                rs.getBoolean("is_blocked"),
                rs.getBoolean("is_premium"),
                rs.getBoolean("is_show_distance"),
                rs.getBoolean("is_invisible"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                Optional.ofNullable(rs.getTimestamp("updated_at"))
                        .map(Timestamp::toLocalDateTime).orElse(null),
                rs.getTimestamp("last_online").toLocalDateTime()
        );
    }
}
