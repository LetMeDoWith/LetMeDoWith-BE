package com.LetMeDoWith.LetMeDoWith.batch.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

@Data
public class DowithTaskDto {
    private Long id;
    private String memberId;
    private Long taskCategoryId;
    private String title;
    private String status;
    private LocalDate date;
    private LocalTime startTime;
    private LocalDateTime successAt;
    private LocalDateTime completeAt;

    public static class RowMapper implements org.springframework.jdbc.core.RowMapper<DowithTaskDto> {
        @Override
        public DowithTaskDto mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            DowithTaskDto dto = new DowithTaskDto();
            dto.setId(rs.getLong("id"));
            dto.setMemberId(rs.getString("member_id"));
            dto.setTaskCategoryId(rs.getLong("task_category_id"));
            dto.setTitle(rs.getString("title"));
            dto.setStatus(rs.getString("status"));
            dto.setDate(rs.getDate("date").toLocalDate());
            dto.setStartTime(rs.getTime("start_time").toLocalTime());
            dto.setSuccessAt(
                    rs.getTimestamp("success_at") != null
                            ? rs.getTimestamp("success_at").toLocalDateTime()
                            : null);
            dto.setCompleteAt(
                    rs.getTimestamp("complete_at") != null
                            ? rs.getTimestamp("complete_at").toLocalDateTime()
                            : null);
            return dto;
        }
    }
}
