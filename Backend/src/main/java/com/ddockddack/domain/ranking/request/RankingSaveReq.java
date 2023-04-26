package com.ddockddack.domain.ranking.request;

import com.ddockddack.domain.game.entity.Game;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.ranking.entity.Ranking;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
public class RankingSaveReq {

    @NotNull(message = "gameId can't be null.")
    private Long gameId;

    @NotNull(message = "image can't be null.")
    private MultipartFile image;

    @NotNull(message = "score can't be null.")
    private Float score;

    public Ranking toEntity(Game game, String imageUrl, Member member){
        return Ranking.builder()
            .game(game)
            .imageUrl(imageUrl)
            .score(score)
            .member(member)
            .build();
    }
}
