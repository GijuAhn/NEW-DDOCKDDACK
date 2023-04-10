package com.ddockddack.domain.gameRoom.request;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class GameSignalReq {
    private String session;
    private List<String> to = new ArrayList<>();
    private String type;
    private String data;

    @Builder
    public GameSignalReq(String session, String type, String data) {
        this.session = session;
        this.type = type;
        this.data = data;
    }
}
