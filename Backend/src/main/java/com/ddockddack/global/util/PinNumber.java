package com.ddockddack.global.util;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class PinNumber {

    private final Integer PIN_NUMBER_BOUND = 1_000_000;
    private final Random random = new Random();

    /**
     * 핀 생성
     *
     * @return
     */
    public String createPinNumber() {
        return formatPin(random.nextInt(PIN_NUMBER_BOUND));
    }

    /**
     * 핀 넘버 포맷팅 
     * @param num
     * @return
     */
    private String formatPin(int num) {
        return String.format("%06d", num);

    }


}
