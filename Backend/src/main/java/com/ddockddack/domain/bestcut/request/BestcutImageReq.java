package com.ddockddack.domain.bestcut.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BestcutImageReq {
    @NotNull(message = "bestcutImg can't be null")
    private int bestcutIndex;

    @NotBlank(message = "bestcutImgTitle can't be blank")
    private String bestcutImgTitle;

    @NotBlank(message = "gameImgUrl can't be blank")
    private String gameImgUrl;
    
    private String gameImgDesc;
}
