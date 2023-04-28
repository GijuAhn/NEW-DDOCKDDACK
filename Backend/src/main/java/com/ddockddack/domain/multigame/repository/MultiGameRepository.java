package com.ddockddack.domain.multigame.repository;

import com.ddockddack.domain.multigame.entity.MultiGame;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MultiGameRepository extends JpaRepository<MultiGame, Long>,
        MultiGameRepositorySupport {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM MultiGame g WHERE g.id in :id")
    void deleteAllByGameId(@Param("id") List<Long> gameId);

    @Modifying
    @Query(value = "UPDATE "
        + "multi_game g,"
        + "(SELECT "
        + "multi_game_id, "
        + "        count(*) as starredCnt"
        + " FROM"
        + " starred_game"
        + " GROUP BY multi_game_id) as s"
        + " SET"
        + " g.starred_cnt = s.starredCnt"
        + " WHERE"
        + " g.multi_game_id = s.multi_game_id;", nativeQuery = true)
    void updateAll();

}
