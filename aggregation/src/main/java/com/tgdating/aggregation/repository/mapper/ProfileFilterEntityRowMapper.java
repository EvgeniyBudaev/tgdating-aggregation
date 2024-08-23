package com.tgdating.aggregation.repository.mapper;

import com.tgdating.aggregation.model.ProfileFilterEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileFilterEntityRowMapper implements RowMapper<ProfileFilterEntity> {
    @Override
    public ProfileFilterEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProfileFilterEntity(
                rs.getLong("id"),
                rs.getString("session_id"),
                rs.getString("search_gender"),
                rs.getString("looking_for"),
                rs.getInt("age_from"),
                rs.getInt("age_to"),
                rs.getDouble("distance"),
                rs.getInt("page"),
                rs.getInt("size")
        );
    }
}
