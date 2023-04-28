package com.ddockddack.domain.multigame.entity;

import com.ddockddack.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StarredGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sttared_game_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "multi_game_id", foreignKey = @ForeignKey(name = "fk_starred_game_game_id_idx", value = ConstraintMode.NO_CONSTRAINT))
    private MultiGame multiGame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_starred_game_member_id_idx"))
    private Member member;

    @Builder
    public StarredGame(MultiGame multiGame, Member member) {
        this.multiGame = multiGame;
        this.member = member;
    }
}
