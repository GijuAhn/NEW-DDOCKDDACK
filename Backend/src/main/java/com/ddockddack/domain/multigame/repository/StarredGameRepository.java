package com.ddockddack.domain.multigame.repository;

import com.ddockddack.domain.multigame.entity.StarredGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StarredGameRepository extends JpaRepository<StarredGame, Long>, StarredGameRepositorySupport{

    Optional<StarredGame> findByMemberIdAndMultiGameId(Long memberId, Long gameId);

    boolean existsByMemberIdAndMultiGameId(Long memberId, Long gameId);

    void deleteByMultiGameId(Long id);

    void deleteByMemberId(Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM StarredGame sg WHERE sg.multiGame.id in :id")
    void deleteAllByGameId(@Param("id") List<Long> gameId);


}
