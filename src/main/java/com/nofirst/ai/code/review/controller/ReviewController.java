package com.nofirst.ai.code.review.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.nofirst.ai.code.review.mapstruct.PushEventMapper;
import com.nofirst.ai.code.review.model.gitlab.MyPushEvent;
import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import com.nofirst.ai.code.review.service.DisruptorService;
import com.nofirst.ai.code.review.service.ReviewConfigService;
import com.nofirst.ai.code.review.service.reviewer.PushEventReviewService;
import com.taobao.api.ApiException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.webhook.Event;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class ReviewController {

    private final ReviewConfigService reviewConfigService;
    private final DisruptorService disruptorService;
    private final PushEventMapper pushEventMapper;
    private final PushEventReviewService pushEventReviewService;


    @PostMapping(value = "/review/webhook/{id}")
    public void review(@RequestHeader(value = "X-Gitlab-Event") String gitlabEvent,
                       @RequestHeader(value = "X-Gitlab-Instance", required = false) String gitlabUrl,
                       @RequestHeader(value = "X-Gitlab-Token", required = false) String gitlabToken,
                       @PathVariable Long id,
                       @RequestBody String jsonStr) throws ApiException, GitLabApiException {

        ReviewConfigInfo reviewConfigInfo = reviewConfigService.fetchValidReviewConfigInfo(id, gitlabUrl, gitlabToken);

        log.info("received review event:{}", jsonStr);

        Event event = null;
        if ("Push Hook".equals(gitlabEvent)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                MyPushEvent myPushEvent = mapper.readValue(jsonStr, MyPushEvent.class);
                event = pushEventMapper.toPushEvent(myPushEvent);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        disruptorService.sendMsg(event, reviewConfigInfo);
    }

    @GetMapping(value = "/review/file")
    public void reviewFile() throws ApiException, GitLabApiException {
        ReviewConfigInfo reviewConfigInfo = reviewConfigService.fetchValidReviewConfigInfo(3L, null, null);
        pushEventReviewService.reviewFile(reviewConfigInfo);
    }
}
