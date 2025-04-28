package com.nofirst.ai.code.review.service.reviewer;

import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfo;
import org.gitlab4j.api.webhook.Event;
import org.gitlab4j.api.webhook.PushEvent;

/**
 * The interface Event reviewer.
 *
 * @param <E> the type parameter
 */
public interface EventReviewer<E extends Event> {

    /**
     * do code review
     *
     * @param pushEvent        the webhook event
     * @param reviewConfig     the review config
     * @param reviewResultInfo
     */
    void review(PushEvent pushEvent, ReviewConfigInfo reviewConfig, ReviewResultInfo reviewResultInfo);
}
