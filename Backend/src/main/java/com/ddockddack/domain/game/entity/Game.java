package com.ddockddack.domain.game.entity;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.report.entity.ReportedGame;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_game_member_idx"))
    private Member member;

    @Column(length = 30, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Category category; // PICTURE

    @Column(length = 50)
    private String description;

    @Column(columnDefinition = "INT default 0")
    private int playCount;

    @Column(columnDefinition = "DATETIME default now()")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private List<GameImage> images;

    @Builder
    public Game(Member member, String title, Category category, String description) {
        this.member = member;
        this.title = title;
        this.category = category;
        this.description = description;
    }

    public void updateGame(String title, String gameDesc) {
        this.title = title;
        this.description = gameDesc;
    }

    public void increasePlayCount() {
        this.playCount++;
    }

}