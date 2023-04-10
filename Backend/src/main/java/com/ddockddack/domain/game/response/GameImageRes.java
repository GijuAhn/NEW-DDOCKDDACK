package com.ddockddack.domain.game.response;

import com.ddockddack.domain.game.entity.GameImage;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class GameImageRes {

    private Long gameImageId;

    private String gameImage;

    private String gameImageDesc;

    @QueryProjection
    public GameImageRes(Long gameImageId, String gameImage, String gameImageDesc) {
        this.gameImageId = gameImageId;
        this.gameImage = gameImage;
        this.gameImageDesc = gameImageDesc;
    }

    public static GameImageRes of(GameImage gameImage) {
        GameImageRes gameImageRes = new GameImageRes();
        gameImageRes.gameImageId = gameImage.getId();
        gameImageRes.gameImage = gameImage.getImageUrl();
        gameImageRes.gameImageDesc = gameImage.getDescription();
        return gameImageRes;
    }


}
