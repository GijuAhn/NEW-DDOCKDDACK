package com.ddockddack.domain.singlegame.response;

import com.ddockddack.domain.singlegame.entity.SingleGame;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class SingleGameRes {

    private Long id;

    private String title;

    private String thumbnail;

    private long playCount;

    public static SingleGameRes from(SingleGame singleGame) {
        SingleGameRes res = new SingleGameRes();
        res.setId(singleGame.getId());
        res.setTitle(singleGame.getTitle());
        res.setThumbnail(singleGame.getThumbnail());
        res.setPlayCount(singleGame.getPlayCount());
        return res;
    }
}