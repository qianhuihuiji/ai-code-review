package com.nofirst.ai.code.review.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.nofirst.ai.code.review.mapstruct.PushEventMapper;
import com.nofirst.ai.code.review.model.enums.TaskReviewStatusEnum;
import com.nofirst.ai.code.review.model.gitlab.MyPushEvent;
import com.nofirst.ai.code.review.repository.dao.IReviewEventTaskDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import com.nofirst.ai.code.review.repository.entity.ReviewEventTask;
import com.nofirst.ai.code.review.service.DisruptorService;
import com.nofirst.ai.code.review.service.ReviewConfigService;
import com.nofirst.ai.code.review.service.reviewer.PushEventReviewService;
import com.nofirst.ai.code.review.service.reviewer.TaskReviewService;
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

import java.util.Date;

@RestController
@Slf4j
@AllArgsConstructor
public class ReviewController {

    private final ReviewConfigService reviewConfigService;
    private final DisruptorService disruptorService;
    private final PushEventMapper pushEventMapper;
    private final PushEventReviewService pushEventReviewService;
    private final TaskReviewService taskReviewService;

    private final IReviewEventTaskDAO reviewResultInfoDAO;


    @PostMapping(value = "/review/webhook/{id}")
    public void review(@RequestHeader(value = "X-Gitlab-Event") String gitlabEvent,
                       @RequestHeader(value = "X-Gitlab-Instance", required = false) String gitlabUrl,
                       @RequestHeader(value = "X-Gitlab-Token", required = false) String gitlabToken,
                       @PathVariable Long id,
                       @RequestBody String jsonStr) throws ApiException, GitLabApiException {

        ReviewConfigInfo reviewConfigInfo = reviewConfigService.fetchValidReviewConfigInfo(id, gitlabUrl, gitlabToken);

        log.info("received review event:{}", jsonStr);

        Event event = null;
        Date dtNow = new Date();
        if ("Push Hook".equals(gitlabEvent)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                MyPushEvent myPushEvent = mapper.readValue(jsonStr, MyPushEvent.class);
                event = pushEventMapper.toPushEvent(myPushEvent);

                ReviewEventTask reviewEventTask = new ReviewEventTask();
                reviewEventTask.setCreateTime(dtNow);
                reviewEventTask.setConfigId(reviewConfigInfo.getId());
                reviewEventTask.setProjectName(reviewConfigInfo.getProjectName());
                reviewEventTask.setReviewStatus(TaskReviewStatusEnum.NOT_STARTED.getStatus());
                reviewResultInfoDAO.save(reviewEventTask);

                disruptorService.sendMsg(event, reviewConfigInfo, reviewEventTask);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }


    }

    @GetMapping(value = "/review/file")
    public void reviewFile() {
        ReviewConfigInfo reviewConfigInfo = reviewConfigService.fetchValidReviewConfigInfo(1L, null, null);
        taskReviewService.reviewFile(reviewConfigInfo,
                "op-starter/src/main/java/com/hnup/osmp/op/starter/service/operator/OpPhaseTimeService.java",
                "refs/heads/dev");
    }

    @GetMapping(value = "/review/project")
    public void reviewProject() {
        ReviewConfigInfo reviewConfigInfo = reviewConfigService.fetchValidReviewConfigInfo(1L, null, null);
        taskReviewService.reviewProject(reviewConfigInfo, "/", "refs/heads/dev");
    }
}
