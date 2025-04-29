package com.nofirst.ai.code.review.model.chat;

import lombok.Data;

@Data
public class Issue {

    private String file;

    private String title;

    private String detail;

    private String severity;

    private String advice;
}