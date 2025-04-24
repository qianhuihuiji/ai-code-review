package com.nofirst.ai.code.review.service.reviewer;

import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import org.gitlab4j.api.webhook.Event;

/**
 * The interface Event reviewer.
 *
 * @param <E> the type parameter
 */
public interface EventReviewer<E extends Event> {

    /**
     * do code review
     *
     * @param event        the webhook event
     * @param reviewConfig the review config
     */
    void review(E event, ReviewConfigInfo reviewConfig);
}
