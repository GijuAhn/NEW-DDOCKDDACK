package com.ddockddack.domain.rank.entity;

import com.ddockddack.domain.game.entity.Game;
import com.ddockddack.domain.member.entity.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_game_score", columnList = "game_id ,score DESC"))
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rank_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", foreignKey = @ForeignKey(name = "fk_rank_game_idx"))
    private Game game;

    @Column(length = 300, nullable = false)
    private String imageUrl;

    @Column
    private Float score;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_rank_member_idx"))
    private Member member;

    @Builder
    public Ranking(Game game, String imageUrl, Float score, Member member) {
        this.game = game;
        this.imageUrl = imageUrl;
        this.score = score;
        this.member = member;
    }
}
