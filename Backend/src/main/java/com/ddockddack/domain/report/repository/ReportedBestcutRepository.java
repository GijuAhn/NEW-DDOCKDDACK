package com.ddockddack.domain.report.repository;

import com.ddockddack.domain.report.entity.ReportedBestcut;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportedBestcutRepository extends JpaRepository<ReportedBestcut, Long> {

    boolean existsByReportMemberIdAndBestcutId(Long memberId, Long bestcutId);

    @Modifying
    @Query("DELETE FROM ReportedBestcut r WHERE r.bestcut.id = :id")
    void deleteByBestcutId(@Param("id") Long bestcutId);

    @Modifying
    @Query("DELETE FROM ReportedBestcut r WHERE r.bestcut.id in :ids")
    void deleteByBestcutIdIn(@Param("ids") List<Long> bestcutIds);

    @Modifying
    @Query("DELETE FROM ReportedBestcut r WHERE r.reportedMember.id = :id OR r.reportMember.id =:id")
    void deleteByMemberId(@Param("id") Long memberId);
}
