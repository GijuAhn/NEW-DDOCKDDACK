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
}
