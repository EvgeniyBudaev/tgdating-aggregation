package com.tgdating.aggregation.repository.mapper;

import com.tgdating.aggregation.model.ProfileListEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileListEntityRowMapper implements RowMapper<ProfileListEntity> {
    @Override
    public ProfileListEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProfileListEntity(
                rs.getString("session_id"),
                rs.getDouble("distance"),
                rs.getTimestamp("last_online").toLocalDateTime()
        );
    }
}
