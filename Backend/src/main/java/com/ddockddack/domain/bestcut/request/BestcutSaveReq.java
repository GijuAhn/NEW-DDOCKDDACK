package com.ddockddack.domain.bestcut.request;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BestcutSaveReq {

    @NotNull(message = "pinNumber can't be null")
    private String pinNumber;

    @NotNull(message = "sessionId can't be null")
    private String socketId;

    @NotBlank(message = "gameTitle can't be blank")
    private String gameTitle;

    @Valid
    @Size(min = 1, max=10, message = "images must have at least 1 and less than 10 photos. ")
    @NotNull(message = "images can't be null")
    private List<BestcutImageReq> images;
}
