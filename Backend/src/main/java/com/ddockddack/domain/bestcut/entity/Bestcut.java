package com.ddockddack.domain.bestcut.entity;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.global.util.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Bestcut extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bestcut_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_bestcut_member_id_idx"))
    private Member member;

    @Column(length = 300, nullable = false)
    private String gameImageUrl;

    @Column(length = 30, nullable = false)
    private String gameTitle;

    @Column(length = 50)
    private String gameImgDesc;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(length = 300, nullable = false)
    private String imageUrl;

    @Column(columnDefinition = "INT default 0")
    private int likeCount;

    @Builder
    public Bestcut(Member member, String gameImageUrl, String gameTitle, String gameImgDesc,
        String title, String imageUrl, int likeCount) {
        this.member = member;
        this.gameTitle = gameTitle;
        this.gameImageUrl = gameImageUrl;
        this.gameImgDesc = gameImgDesc;
        this.imageUrl = imageUrl;
        this.title = title;
        this.likeCount = likeCount;
    }
}
