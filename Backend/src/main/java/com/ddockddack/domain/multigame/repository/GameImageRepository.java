package com.ddockddack.domain.multigame.repository;

import com.ddockddack.domain.multigame.entity.GameImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameImageRepository extends JpaRepository<GameImage, Long> {

    void deleteByMultiGameId(Long id);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM GameImage gi WHERE gi.multiGame.id in :id")
    void deleteAllByGameId(@Param("id") List<Long> multiGameId);

}
