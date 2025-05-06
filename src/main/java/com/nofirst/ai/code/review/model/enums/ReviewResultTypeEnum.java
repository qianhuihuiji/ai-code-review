package com.nofirst.ai.code.review.model.enums;

import lombok.Getter;

@Getter
public enum ReviewResultTypeEnum {

    NOT_STARTED(0, "未开始"),

    SUCCESS(1, "成功"),

    FAIL(2, "失败");

    private final Integer status;

    private final String description;

    ReviewResultTypeEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }
}
