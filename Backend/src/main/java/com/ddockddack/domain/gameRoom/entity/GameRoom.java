package com.ddockddack.domain.gameRoom.entity;

import com.ddockddack.domain.game.entity.GameImage;
import com.ddockddack.domain.game.response.GameImageRes;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "gameRoom", timeToLive = 3600)
public class GameRoom {

    @Id
    private String pinNumber;
    private Long gameId;
    private String gameTitle;
    private String gameDescription;
    private List<GameImageRes> gameImages;
    private boolean isStarted;
    private int scoreCount = 0;
    private int round = 1;

    @Builder
    public GameRoom(String pinNumber, Long gameId, String gameTitle, String gameDescription,
        List<GameImage> gameImages) {
        this.pinNumber = pinNumber;
        this.gameId = gameId;
        this.gameTitle = gameTitle;
        this.gameDescription = gameDescription;
        this.gameImages = gameImages.stream()
            .map(i -> GameImageRes.of(i))
            .collect(Collectors.toList());
    }

    public void start() {
        this.isStarted = true;
    }

    public void increaseScoreCnt() {
        this.scoreCount++;
    }

    public void resetScoreCnt() {
        this.scoreCount = 0;
    }

    public void increaseRound() {
        this.round++;
    }
}
