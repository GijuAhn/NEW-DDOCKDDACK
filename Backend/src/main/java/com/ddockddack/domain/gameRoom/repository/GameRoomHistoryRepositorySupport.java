package com.ddockddack.domain.gameRoom.repository;

import static com.ddockddack.domain.gameRoom.entity.QGameRoomHistory.gameRoomHistory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRoomHistoryRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    // 게임 이력 삭제
    public List<Long> findAllGameRoomHistoryIdByMemberId(Long memberId) {
        return jpaQueryFactory
            .select(gameRoomHistory.id
            )
            .from(gameRoomHistory)
            .where(gameRoomHistory.memberId.eq(memberId))
            .fetch();
    }
}
