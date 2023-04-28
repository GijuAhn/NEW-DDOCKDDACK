package com.ddockddack.domain.multigame.request;

import com.ddockddack.domain.multigame.entity.MultiGame;
import com.ddockddack.domain.multigame.entity.GameImage;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class GameImageParam {

    @NotNull(message = "gameImage cannot be empty.")
    private MultipartFile gameImage;

    @Size(max = 50, message = "The maximum length of the gameImageDesc is 50.")
    private String gameImageDesc;

    public GameImage toEntity(MultiGame multiGame, String imageUrl) {
        return GameImage.builder()
            .multiGame(multiGame)
            .imageUrl(imageUrl)
            .description(this.gameImageDesc)
            .build();
    }

}
