package com.ddockddack.domain.gameRoom.repository;

import static com.ddockddack.domain.gameRoom.entity.QGameRoomHistory.gameRoomHistory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

public interface GameRoomHistoryRepositorySupport {

    List<Long> findAllGameRoomHistoryIdByMemberId(Long memberId);
}
