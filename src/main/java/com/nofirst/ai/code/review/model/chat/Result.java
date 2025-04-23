package com.nofirst.ai.code.review.model.chat;

import lombok.Data;

import java.util.List;

@Data
public class Result {

    private String type;

    private int score;

    private List<Question> questions;

    private List<String> advices;
}