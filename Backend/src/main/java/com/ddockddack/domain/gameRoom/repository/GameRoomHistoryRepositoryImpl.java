package com.ddockddack.domain.gameRoom.repository;

import static com.ddockddack.domain.gameRoom.entity.QGameRoomHistory.gameRoomHistory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRoomHistoryRepositoryImpl implements GameRoomHistoryRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Long> findAllGameRoomHistoryIdByMemberId(Long memberId) {
        return jpaQueryFactory
            .select(gameRoomHistory.id
            )
            .from(gameRoomHistory)
            .where(gameRoomHistory.memberId.eq(memberId))
            .fetch();
    }
}
