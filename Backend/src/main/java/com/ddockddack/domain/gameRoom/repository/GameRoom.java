package com.ddockddack.domain.gameRoom.repository;

import com.ddockddack.domain.game.entity.GameImage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GameRoom {
    private String pinNumber;
    private Long gameId;
    private String gameTitle;
    private String gameDescription;
    private List<GameImage> gameImages;

    private boolean isStarted;

    private int scoreCount = 0;
    private int round = 1;

    private Map<String, GameMember> members = new ConcurrentHashMap<>();

    @Builder
    public GameRoom(String pinNumber, Long gameId, String gameTitle, String gameDescription,
            List<GameImage> gameImages) {
        this.pinNumber = pinNumber;
        this.gameId = gameId;
        this.gameTitle = gameTitle;
        this.gameDescription = gameDescription;
        this.gameImages = gameImages;
    }

    public void start() {
        this.isStarted = true;
    }

    public void increaseScoreCnt(){
        this.scoreCount++;
    }
    public void resetScoreCnt(){this.scoreCount = 0;}
    public void increaseRound(){this.round++;}
}
