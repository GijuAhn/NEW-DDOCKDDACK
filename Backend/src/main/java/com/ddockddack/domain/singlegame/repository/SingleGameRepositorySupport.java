package com.ddockddack.domain.singlegame.repository;

import com.ddockddack.domain.singlegame.entity.SingleGame;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface SingleGameRepositorySupport {
    PageImpl<SingleGame> findSingleGames(String keyword, Pageable pageable);
}
