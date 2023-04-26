package com.ddockddack.domain.ranking.response;

import com.ddockddack.domain.ranking.entity.Ranking;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RankingRes {

    private Integer index;
    private Float score;
    private String imageUrl;
    private String nickname;

    public static RankingRes from(int index, Ranking ranking) {
        RankingRes res = new RankingRes();
        res.index = index;
        res.score = ranking.getScore();
        res.imageUrl = ranking.getImageUrl();
        res.nickname = ranking.getMember().getNickname();
        return res;
    }
}
