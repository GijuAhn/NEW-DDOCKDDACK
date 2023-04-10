package com.ddockddack.domain.game.repository;

import com.ddockddack.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Game g WHERE g.id in :id")
    void deleteAllByGameId(@Param("id") List<Long> gameId);

}
