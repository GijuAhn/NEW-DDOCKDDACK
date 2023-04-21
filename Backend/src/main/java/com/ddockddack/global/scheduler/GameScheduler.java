package com.ddockddack.global.scheduler;

import com.ddockddack.domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class GameScheduler {

    private final GameRepository gameRepository;

    @Scheduled(cron = "00 00 04 ? * *",zone = "Asia/Seoul")
    public void updateAllGames() {
        gameRepository.updateAll();
    }
}
