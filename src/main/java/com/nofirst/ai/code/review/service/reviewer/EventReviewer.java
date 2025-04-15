package com.nofirst.ai.code.review.service.reviewer;

import org.gitlab4j.api.webhook.Event;

/**
 * The interface Event reviewer.
 */
public interface EventReviewer<E extends Event> {

    /**
     * do code review
     *
     * @param event       the webhook event
     * @param gitlabUrl   the gitlab url
     * @param gitlabToken the gitlab token
     */
    void review(E event, String gitlabUrl, String gitlabToken);
}
