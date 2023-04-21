package com.ddockddack.domain.game.repository;

import com.ddockddack.domain.game.entity.Game;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>, GameRepositorySupport {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Game g WHERE g.id in :id")
    void deleteAllByGameId(@Param("id") List<Long> gameId);

    @Modifying
    @Query(value = "UPDATE "
        + "game g,"
        + "(SELECT "
        + "game_id, "
        + "        count(*) as starredCnt"
        + " FROM"
        + " starred_game"
        + " GROUP BY game_id) as s"
        + " SET"
        + " g.starred_cnt = s.starredCnt"
        + " WHERE"
        + " g.game_id = s.game_id;", nativeQuery = true)
    void updateAll();

}
