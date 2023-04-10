package com.ddockddack.domain.gameRoom.response;

import com.ddockddack.domain.gameRoom.repository.GameMember;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class GameMemberRes {
    private String socketId;
    private String nickname;
    private byte[] image;
    private int roundScore;
    private int scaledRoundScore;
    private int totalScore;

//    매 라운드가 끝날때마다 response 반환, 마지막 라운드에는 누적값(totalScore)를 roundScore 에 담아 반환.
    @Builder
    public GameMemberRes(String socketId, String nickname, byte[] image, int roundScore, int scaledRoundScore, int totalScore) {
        this.socketId = socketId;
        this.image = image;
        this.roundScore = roundScore;
        this.scaledRoundScore = scaledRoundScore;
        this.totalScore = totalScore;
        this.nickname = nickname;
    }

    public static GameMemberRes from(GameMember gameMember, int round){
        return GameMemberRes.builder()
                .socketId(gameMember.getSocketId())
                .nickname(gameMember.getNickname())
                .image(gameMember.getImages().get(round))
                .roundScore(gameMember.getRoundScore())
                .scaledRoundScore(gameMember.getScaledRoundScore())
                .build();
    }

    public static GameMemberRes from(GameMember gameMember){
        return GameMemberRes.builder()
                .socketId(gameMember.getSocketId())
                .nickname(gameMember.getNickname())
                .totalScore(gameMember.getTotalScore())
                .build();
    }
}
