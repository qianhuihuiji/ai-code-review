package com.nofirst.ai.code.review.controller;

import com.nofirst.ai.code.review.service.DisruptorService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.webhook.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ReviewController {

    @Autowired
    private DisruptorService disruptorService;

    @PostMapping(value = "/review/webhook")
    public void review(@RequestHeader(value = "X-Gitlab-Instance") String gitlabUrl,
                       @RequestHeader(value = "X-Gitlab-Token") String gitlabToken,
                       @RequestBody Event event) throws ApiException, GitLabApiException {
        log.info("review event:{}", event);
        disruptorService.sendMsg(event, gitlabUrl, gitlabToken);
    }
}
