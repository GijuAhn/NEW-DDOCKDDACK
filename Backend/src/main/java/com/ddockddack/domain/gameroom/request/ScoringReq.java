package com.ddockddack.domain.gameroom.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScoringReq {
    @NotEmpty
    private String pinNumber;
    @NotEmpty
    private String socketId;
    @NotEmpty
    private String gameImage;
    @NotNull
    private MultipartFile memberGameImage;
}
