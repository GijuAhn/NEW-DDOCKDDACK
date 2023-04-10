package com.ddockddack.domain.game.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class GameModifyReq {

    @NotNull(message = "gameId is not null.")
    private Long gameId;

    private String gameTitle;

    private String gameDesc;

    private List<GameImageModifyReq> images;
}
