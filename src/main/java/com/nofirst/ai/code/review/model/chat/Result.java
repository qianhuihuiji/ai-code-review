package com.nofirst.ai.code.review.model.chat;

import lombok.Data;

import java.util.List;

@Data
public class Result {

    private String dimension;

    private int score;

    private int fullScore;

    private List<Issue> issues;

}