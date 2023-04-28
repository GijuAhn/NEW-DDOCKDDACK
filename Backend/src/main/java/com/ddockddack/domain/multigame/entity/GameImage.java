package com.ddockddack.domain.multigame.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "multi_game_id", foreignKey = @ForeignKey(name = "fk_game_image_game_id_idx"))
    private MultiGame multiGame;

    @Column(length = 300, nullable = false)
    private String imageUrl;

    @Column(length = 50)
    private String description;

    @Builder
    public GameImage(MultiGame multiGame, String imageUrl, String description) {
        this.multiGame = multiGame;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public void updateGameImage(String gameImageUrl, String gameImageDesc) {
        this.imageUrl = gameImageUrl;
        this.description = gameImageDesc;
    }

}
