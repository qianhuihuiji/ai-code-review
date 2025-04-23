package com.nofirst.ai.code.review.model.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class EvaluationReport {

    private List<Result> results;

    private int totalScore;

    @JsonIgnore
    private String markdownContent;
}