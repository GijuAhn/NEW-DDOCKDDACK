package com.ddockddack.domain.gameRoom.repository;

import com.ddockddack.domain.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class GameMember {

    private String socketId;
    private Member member;
    private String nickname;
    private Integer roundScore = 0;
    private Integer scaledRoundScore = 0;
    private Integer totalScore = 0;
    private String clientIp;
    private List<byte[]> images = new ArrayList<>();

    @Builder
    public GameMember(String socketId, Member member, String nickname, String clientIp) {
        this.socketId = socketId;
        this.member = member;
        this.nickname = nickname;
        this.clientIp = clientIp;
    }

    public void changeRoundScore(int roundScore){
        this.roundScore = roundScore;
    }

    public void changeScaledRoundScore(int scaledRoundScore){
        this.scaledRoundScore = scaledRoundScore;
    }

    public void changeTotalScore(int totalScore){
        this.totalScore = totalScore;
    }
}
