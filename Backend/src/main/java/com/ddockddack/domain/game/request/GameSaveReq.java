package com.ddockddack.domain.game.request;

import com.ddockddack.domain.game.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class GameSaveReq {

    @NotNull(message = "gameTitle cannot be empty.")
    @Size(max = 30, message = "The maximum length of the gameTitle is 30.")
    private String gameTitle;

    @NotNull(message = "gameCategory cannot be empty.")
    private Category gameCategory;

    @Size(max = 50, message = "The maximum length of the gameDesc is 50.")
    private String gameDesc;

    @Valid
    @Size(min = 10, max = 20, message = "Check the gameImage length.")
    private List<GameImageParam> images;

}
