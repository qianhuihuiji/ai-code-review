package com.nofirst.ai.code.review.model.enums;

import lombok.Getter;

@Getter
public enum ProjectTaskReviewStatusEnum {

    NOT_STARTED(0, "未开始"),

    ALL_SUCCESS(1, "全部成功"),

    PART_SUCCESS(2, "部分成功");

    private final Integer status;

    private final String description;

    ProjectTaskReviewStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }
}
