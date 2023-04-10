package com.ddockddack.domain.bestcut.repository;

import com.ddockddack.domain.bestcut.entity.BestcutLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BestcutLikeRepository extends JpaRepository<BestcutLike, Long> {

    Optional<BestcutLike> findByMemberIdAndBestcutId(Long memberId, Long bestcutId);

    boolean existsByMemberIdAndBestcutId(Long memberId, Long bestcutId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BestcutLike l WHERE l.bestcut.id = :id")
    void deleteByBestcutId(@Param("id") Long bestcutId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BestcutLike l WHERE l.bestcut.id in :ids")
    void deleteByBestcutIdIn(@Param("ids") List<Long> bestcutIds);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BestcutLike l WHERE l.member.id = :id")
    void deleteByMemberId(@Param("id") Long memberId);
}
