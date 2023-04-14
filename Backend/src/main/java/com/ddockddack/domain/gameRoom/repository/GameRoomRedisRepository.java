package com.ddockddack.domain.gameRoom.repository;

import com.ddockddack.domain.gameRoom.entity.GameRoom;
import org.springframework.data.repository.CrudRepository;

public interface GameRoomRedisRepository extends CrudRepository<GameRoom, String> {
}
