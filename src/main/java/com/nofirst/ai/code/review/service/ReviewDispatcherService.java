package com.nofirst.ai.code.review.service;

import com.nofirst.ai.code.review.disruptor.MessageModel;
import com.nofirst.ai.code.review.service.reviewer.PushEventReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.webhook.Event;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.stereotype.Service;

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
     * @param messageModel the message model
     */
    public void review(MessageModel messageModel) {
        Event event = messageModel.getEvent();
        if (event instanceof PushEvent) {
            PushEvent pushEvent = (PushEvent) event;
            pushEventReviewService.review(pushEvent, messageModel.getReviewConfigInfo());
        }
    }


}
