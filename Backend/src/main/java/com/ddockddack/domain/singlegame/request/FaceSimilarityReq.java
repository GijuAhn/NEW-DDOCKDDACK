package com.ddockddack.domain.singlegame.request;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class FaceSimilarityReq {
    @NotNull(message = "source image can't be null.")
    private MultipartFile source;
    @NotNull(message = "target image can't be null.")
    private String target;
}
