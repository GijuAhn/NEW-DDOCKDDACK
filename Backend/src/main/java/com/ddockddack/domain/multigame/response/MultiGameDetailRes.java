package com.ddockddack.domain.multigame.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class MultiGameDetailRes {

    private Long gameId;
    private String gameTitle;
    private String gameDesc;
    private List<GameImageRes> images;

    @QueryProjection
    public MultiGameDetailRes(Long gameId, String gameTitle, String gameDesc, List<GameImageRes> images) {
        this.gameId = gameId;
        this.gameTitle = gameTitle;
        this.gameDesc = gameDesc;
        this.images = images;
    }
}
