package com.ddockddack.domain.game.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class GameImageParam {

    @NotNull(message = "gameImage cannot be empty.")
    private MultipartFile gameImage;

    @Size(max = 50, message = "The maximum length of the gameImageDesc is 50.")
    private String gameImageDesc;

}
