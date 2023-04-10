package com.ddockddack.domain.game.repository;

import com.ddockddack.domain.game.entity.StarredGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StarredGameRepository extends JpaRepository<StarredGame, Long> {

    Optional<StarredGame> findByMemberIdAndGameId(Long memberId, Long gameId);

    boolean existsByMemberIdAndGameId(Long memberId, Long gameId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM StarredGame sg WHERE sg.game.id = :id")
    void deleteByGameId(Long id);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM StarredGame sg WHERE sg.member.id = :memberId")
    void deleteByMemberId(Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM StarredGame sg WHERE sg.game.id in :id")
    void deleteAllByGameId(@Param("id") List<Long> gameId);


}
