package com.tgdating.aggregation.repository.mapper;

import com.tgdating.aggregation.model.ProfileTelegramEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileTelegramEntityRowMapper implements RowMapper<ProfileTelegramEntity> {
    @Override
    public ProfileTelegramEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProfileTelegramEntity(
                rs.getLong("id"),
                rs.getString("session_id"),
                rs.getLong("user_id"),
                rs.getString("username"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("language_code"),
                rs.getBoolean("allows_write_to_pm"),
                rs.getString("query_id"),
                rs.getLong("chat_id")
        );
    }
}
