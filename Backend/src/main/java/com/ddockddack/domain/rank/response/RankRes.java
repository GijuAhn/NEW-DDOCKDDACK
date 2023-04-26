package com.ddockddack.domain.rank.response;

import com.ddockddack.domain.rank.entity.Ranking;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RankRes {

    private Integer index;
    private Float score;
    private String imageUrl;
    private String nickname;

    public static RankRes from(int index, Ranking ranking) {
        RankRes res = new RankRes();
        res.index = index;
        res.score = ranking.getScore();
        res.imageUrl = ranking.getImageUrl();
        res.nickname = ranking.getMember().getNickname();
        return res;
    }
}
