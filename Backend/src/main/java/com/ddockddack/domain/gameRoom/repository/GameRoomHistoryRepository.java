package com.ddockddack.domain.gameRoom.repository;

import com.ddockddack.domain.gameRoom.entity.GameRoomHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRoomHistoryRepository extends JpaRepository<GameRoomHistory, Long> {

    List<GameRoomHistory> findByMemberId(Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM GameRoomHistory gh WHERE gh.id in :id")
    void deleteAllByGameId(@Param("id") List<Long> gameRoomHistoryId);
}
