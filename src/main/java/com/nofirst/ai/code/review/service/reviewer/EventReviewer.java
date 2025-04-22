package com.nofirst.ai.code.review.service.reviewer;

import org.gitlab4j.api.webhook.Event;

import java.util.Date;

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
     * @param dtNow       dtNow
     */
    void review(E event, String gitlabUrl, String gitlabToken, Date dtNow);
}
