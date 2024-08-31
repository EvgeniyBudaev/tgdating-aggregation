package com.tgdating.aggregation.repository.mapper;

import com.tgdating.aggregation.model.PointEntity;
import com.tgdating.aggregation.model.ProfileNavigatorEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class ProfileNavigatorEntityRowMapper implements RowMapper<ProfileNavigatorEntity> {
    @Override
    public ProfileNavigatorEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        PointEntity location = PointEntity.builder()
                .latitude(rs.getDouble("latitude"))
                .longitude(rs.getDouble("longitude"))
                .build();

        return new ProfileNavigatorEntity(
                rs.getLong("id"),
                rs.getString("session_id"),
                location,
                rs.getTimestamp("created_at").toLocalDateTime(),
                Optional.ofNullable(rs.getTimestamp("updated_at"))
                        .map(Timestamp::toLocalDateTime).orElse(null)
        );
    }
}
