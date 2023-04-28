package com.ddockddack.domain.ranking.response;

import com.ddockddack.domain.ranking.entity.Ranking;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RankingRes {

    private Float score;
    private String imageUrl;
    private String nickname;

    public static RankingRes from(Ranking ranking) {
        RankingRes res = new RankingRes();
        res.setScore(ranking.getScore());
        res.setImageUrl(ranking.getImageUrl());
        res.setNickname(ranking.getMember().getNickname());
        return res;
    }
}
