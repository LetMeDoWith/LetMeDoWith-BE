package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskLikeMemberQueryDto;
import java.util.List;

public record RetrieveDowithTaskLikersResult(long totalCount, List<DowithTaskLiker> likers) {

    public static RetrieveDowithTaskLikersResult of(long totalCount, List<DowithTaskLikeMemberQueryDto> rows) {
        List<DowithTaskLiker> likers =
                rows.stream().map(DowithTaskLiker::fromQueryRow).toList();
        return new RetrieveDowithTaskLikersResult(totalCount, likers);
    }

    public record DowithTaskLiker(Long dowithTaskLikeId, String memberId, String nickname, String profileImageUrl) {

        private static DowithTaskLiker fromQueryRow(DowithTaskLikeMemberQueryDto row) {
            return new DowithTaskLiker(row.dowithTaskLikeId(), row.memberId(), row.nickname(), row.profileImageUrl());
        }
    }
}
