package com.ddockddack.domain.report.repository;

import com.ddockddack.domain.report.entity.ReportedRanking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportedRankingRepository extends JpaRepository<ReportedRanking, Long>,
    ReportedRankingRepositorySupport {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ReportedRanking rr WHERE rr.ranking.id in :id")
    void deleteAllByRankingId(@Param("id") List<Long> rankingIds);

}
