package com.nofirst.ai.code.review.service.reviewer;

import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.webhook.MergeRequestEvent;
import org.springframework.stereotype.Service;

/**
 * The type Merge request event review service.
 */
@Service
@Slf4j
@AllArgsConstructor
public class MergeRequestEventReviewService implements EventReviewer<MergeRequestEvent> {


    @Override
    public void review(MergeRequestEvent mergeRequestEvent, ReviewConfigInfo reviewConfig) {
        log.info("Merge Request Hook event received");
        // do nothing
    }

}
