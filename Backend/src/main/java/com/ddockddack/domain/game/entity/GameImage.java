package com.ddockddack.domain.game.entity;

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
    @JoinColumn(name = "game_id", foreignKey = @ForeignKey(name = "fk_game_image_game_id_idx"))
    private Game game;

    @Column(length = 300, nullable = false)
    private String imageUrl;

    @Column(length = 50)
    private String description;

    @Builder
    public GameImage(Game game, String imageUrl, String description) {
        this.game = game;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public void updateGameImage(String gameImageUrl, String gameImageDesc) {
        this.imageUrl = gameImageUrl;
        this.description = gameImageDesc;
    }

}
