package com.ddockddack.domain.bestcut.repository;

import com.ddockddack.domain.bestcut.entity.Bestcut;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BestcutRepository extends JpaRepository<Bestcut, Long>, BestcutRepositorySupport {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Bestcut b WHERE b.id in :id")
    void deleteAllById(@Param("id") List<Long> bestcutId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Bestcut b SET b.likeCount = b.likeCount - 1 WHERE b.id = :bestcutId")
    void minusByBestcutId(Long bestcutId);

    @Modifying(clearAutomatically = true)
    @Query(value = "update bestcut bc " +
            "set like_count = " +
            "(select count(bestcut_id) as cnt " +
            "    from bestcut_like bl " +
            "    where bc.bestcut_id = bl.bestcut_id " +
            "    group by bestcut_id);", nativeQuery = true)
    void syncLikeCount();

}
