package com.ddockddack.domain.report.repository;

import com.ddockddack.domain.report.entity.ReportedGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportedGameRepository extends JpaRepository<ReportedGame, Long> {

    boolean existsByReportMemberIdAndGameId(Long memberId, Long gameId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ReportedGame rg WHERE rg.game.id = :id")
    void deleteByGameId(Long id);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ReportedGame rg WHERE rg.reportMember.id = :id OR rg.reportedMember.id = :id")
    void deleteByMemberId(Long id);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ReportedGame rg WHERE rg.game.id in :id")
    void deleteAllByGameId(@Param("id") List<Long> gameId);
}
