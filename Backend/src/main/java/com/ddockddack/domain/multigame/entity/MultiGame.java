package com.ddockddack.domain.multigame.entity;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.global.util.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class MultiGame extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "multi_game_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_game_member_idx"))
    private Member member;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(length = 50)
    private String description;

    @Column(columnDefinition = "INT default 0")
    private int playCount;

    @OneToMany(mappedBy = "multiGame", fetch = FetchType.LAZY)
    private List<GameImage> images;

    @Column(length = 300, nullable = false)
    private String thumbnail;

    @Column()
    @ColumnDefault("0")
    private int starredCnt;

    @Builder
    public MultiGame(Member member, String title, String description, String thumbnail) {
        this.member = member;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public void updateGame(String title, String gameDesc) {
        this.title = title;
        this.description = gameDesc;
    }

    public void increasePlayCount() {
        this.playCount++;
    }
    public void increaseStarredCnt() {this.starredCnt ++;}
    public void decreaseStarredCnt() { this.starredCnt--;}


}