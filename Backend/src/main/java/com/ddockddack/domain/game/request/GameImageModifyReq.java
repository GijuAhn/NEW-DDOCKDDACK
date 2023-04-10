package com.ddockddack.domain.game.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class GameImageModifyReq {

    private Long gameImageId;

    private MultipartFile gameImage;

    private String gameImageDesc;

}
