package com.nofirst.ai.code.review.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.nofirst.ai.code.review.service.ReviewService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.webhook.AbstractEvent;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ReviewController {


    @Autowired
    private ReviewService reviewService;

    @PostMapping(value = "/review/webhook")
    public void review(@RequestHeader(value = "X-Gitlab-Instance") String gitlabUrl,
                       @RequestHeader(value = "X-Gitlab-Token") String gitlabToken,
                       @RequestBody PushEvent event) throws ApiException, GitLabApiException {
        log.info("review event:{}", event);
        reviewService.review(event,gitlabUrl, gitlabToken);
    }
}
