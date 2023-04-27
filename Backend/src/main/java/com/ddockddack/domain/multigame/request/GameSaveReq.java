package com.ddockddack.domain.multigame.request;

import com.ddockddack.domain.multigame.entity.MultiGame;
import com.ddockddack.domain.member.entity.Member;
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

    @Size(max = 50, message = "The maximum length of the gameDesc is 50.")
    private String gameDesc;

    @Valid
    @Size(min = 10, max = 20, message = "Check the gameImage length.")
    private List<GameImageParam> images;

    public MultiGame toEntity(Member member, String thumbnail) {
        return MultiGame.builder()
            .member(member)
            .title(this.gameTitle)
            .description(this.gameDesc)
            .thumbnail(thumbnail)
            .build();
    }

}
