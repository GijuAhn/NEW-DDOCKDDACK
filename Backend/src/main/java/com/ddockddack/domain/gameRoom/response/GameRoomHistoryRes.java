package com.ddockddack.domain.gameRoom.response;

import com.ddockddack.domain.gameRoom.entity.GameRoomHistory;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GameRoomHistoryRes {

    private String gameTitle;

    private int ranking;

    private LocalDate playedTime;

    @Builder
    public GameRoomHistoryRes(String gameTitle, int ranking, LocalDate playedTime) {
        this.gameTitle = gameTitle;
        this.ranking = ranking;
        this.playedTime = playedTime;
    }

    public static GameRoomHistoryRes of(GameRoomHistory gameRoomHistory) {
        return GameRoomHistoryRes
            .builder()
            .gameTitle(gameRoomHistory.getGameTitle())
            .ranking(gameRoomHistory.getRanking())
            .playedTime(gameRoomHistory.getCreateAt())
            .build();
    }

}
