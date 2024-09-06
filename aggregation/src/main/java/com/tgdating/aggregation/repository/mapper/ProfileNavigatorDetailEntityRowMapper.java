package com.tgdating.aggregation.repository.mapper;

import com.tgdating.aggregation.model.ProfileNavigatorDetailEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileNavigatorDetailEntityRowMapper implements RowMapper<ProfileNavigatorDetailEntity> {
    @Override
    public ProfileNavigatorDetailEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProfileNavigatorDetailEntity(
                rs.getDouble("distance")
        );
    }
}
