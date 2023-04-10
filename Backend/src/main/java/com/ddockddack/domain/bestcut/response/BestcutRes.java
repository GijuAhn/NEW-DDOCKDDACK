package com.ddockddack.domain.bestcut.response;


import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "베스트컷 목록 및 조회 응답 DTO")
public class BestcutRes {

    private Long bestcutId;
    private String bestcutImgTitle;
    private String bestcutImgUrl;
    private String gameTitle;
    @Schema(description = "베스트컷에 해당하는 원본 이미지 링크")
    private String gameImgUrl;
    @Schema(description = "베스트컷에 해당하는 원본 이미지 설명")
    private String gameImgDesc;
    private LocalDateTime createdDate;
    @Schema(description = "좋아요 수")
    private Integer popularity;
    @Schema(description = "로그인 한 유저의 베스트컷 좋아요 여부")
    private Integer isLiked;
    @Schema(description = "베스트컷 작성자 프로필 이미지")
    private String profileImgUrl;
    @Schema(description = "베스트컷 작성자 닉네임")
    private String nickname;

    @QueryProjection
    public BestcutRes(Long bestcutId, String bestcutImgTitle, String bestcutImgUrl,
            String gameTitle, String gameImgUrl, String gameImgDesc, LocalDateTime createdDate,
            Integer popularity, Integer isLiked, String profileImgUrl, String nickname) {
        this.bestcutId = bestcutId;
        this.bestcutImgTitle = bestcutImgTitle;
        this.bestcutImgUrl = bestcutImgUrl;
        this.gameTitle = gameTitle;
        this.gameImgUrl = gameImgUrl;
        this.gameImgDesc = gameImgDesc;
        this.createdDate = createdDate;
        this.popularity = popularity;
        this.isLiked = isLiked;
        this.profileImgUrl = profileImgUrl;
        this.nickname = nickname;
    }
}
