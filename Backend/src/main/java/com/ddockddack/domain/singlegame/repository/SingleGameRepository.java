package com.ddockddack.domain.singlegame.repository;

import com.ddockddack.domain.singlegame.entity.SingleGame;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingleGameRepository extends JpaRepository<SingleGame, Long>, SingleGameRepositorySupport {
    PageImpl<SingleGame> findByTitleContains(String title, Pageable pageable);
}
