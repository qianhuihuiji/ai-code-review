package com.nofirst.ai.code.review.service;

import com.nofirst.ai.code.review.service.reviewer.PushEventReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.webhook.Event;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * dispatch webhook event to different ReviewService
 */
@Service
@Slf4j
@AllArgsConstructor
public class ReviewDispatcherService {

    private final PushEventReviewService pushEventReviewService;

    /**
     * Review.
     *
     * @param event       the event
     * @param gitlabUrl   the gitlab url
     * @param gitlabToken the gitlab token
     */
    public void review(Event event, String gitlabUrl, String gitlabToken) {
        Date date = new Date();
        if (event instanceof PushEvent pushEvent) {
            pushEventReviewService.review(pushEvent, gitlabUrl, gitlabToken, date);
        }
    }


}
