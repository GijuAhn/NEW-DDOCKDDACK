package com.ddockddack.domain.member.service;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BanLevel {
    noPenalty,
    oneWeek,
    oneMonth,
    sixMonth,
    oneYear,
    endless
}
