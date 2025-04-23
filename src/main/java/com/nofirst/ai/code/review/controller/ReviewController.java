package com.nofirst.ai.code.review.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.nofirst.ai.code.review.config.GitlabConfiguration;
import com.nofirst.ai.code.review.mapstruct.PushEventMapper;
import com.nofirst.ai.code.review.model.gitlab.MyPushEvent;
import com.nofirst.ai.code.review.service.DisruptorService;
import com.taobao.api.ApiException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.webhook.Event;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class ReviewController {

    private final DisruptorService disruptorService;
    private final GitlabConfiguration gitlabConfiguration;
    private final PushEventMapper pushEventMapper;


    @PostMapping(value = "/review/webhook")
    public void review(@RequestHeader(value = "X-Gitlab-Instance", required = false) String gitlabUrl,
                       @RequestHeader(value = "X-Gitlab-Token", required = false) String gitlabToken,
                       @RequestHeader(value = "X-Gitlab-Event", required = false) String gitlabEvent,
                       @RequestBody String jsonStr) throws ApiException, GitLabApiException {
        log.info("review event:{}", jsonStr);

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

        if (!StringUtils.hasText(gitlabUrl)) {
            if (StringUtils.hasText(gitlabConfiguration.getUrl())) {
                gitlabUrl = gitlabConfiguration.getUrl();
            } else {
                throw new RuntimeException("gitlab url missed");
            }
        }
        if (!StringUtils.hasText(gitlabToken)) {
            if (StringUtils.hasText(gitlabConfiguration.getToken())) {
                gitlabToken = gitlabConfiguration.getToken();
            } else {
                throw new RuntimeException("gitlab token missed");
            }
        }
        disruptorService.sendMsg(event, gitlabUrl, gitlabToken);
    }
}
