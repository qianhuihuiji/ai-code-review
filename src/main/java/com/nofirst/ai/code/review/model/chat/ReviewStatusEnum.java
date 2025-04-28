package com.nofirst.ai.code.review.model.chat;

import lombok.Getter;

@Getter
public enum ReviewStatusEnum {

    NOT_STARTED(0, "未开始"),

    SUCCESS(1, "成功"),

    FAIL(2, "失败");

    private final Integer status;

    private final String description;

    ReviewStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }
}
