package com.nofirst.ai.code.review.service.reviewer;

import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.webhook.MergeRequestEvent;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.stereotype.Service;

/**
 * The type Merge request event review service.
 */
@Service
@Slf4j
@AllArgsConstructor
public class MergeRequestEventReviewService implements EventReviewer<MergeRequestEvent> {


    @Override
    public void review(PushEvent mergeRequestEvent, ReviewConfigInfo reviewConfig, ReviewResultInfo reviewResultInfo) {
        log.info("Merge Request Hook event received");
        // do nothing
    }

}
