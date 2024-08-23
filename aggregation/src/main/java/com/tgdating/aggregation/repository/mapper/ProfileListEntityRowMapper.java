package com.tgdating.aggregation.repository.mapper;

import com.tgdating.aggregation.dto.response.ResponseProfileListGetDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileListEntityRowMapper implements RowMapper<ResponseProfileListGetDto> {
    @Override
    public ResponseProfileListGetDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ResponseProfileListGetDto(
                rs.getString("session_id")
        );
    }
}
