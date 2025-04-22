package com.nofirst.ai.code.review.service.reviewer;

import com.nofirst.ai.code.review.service.DeepseekService;
import com.nofirst.ai.code.review.service.DingDingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.webhook.MergeRequestEvent;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * The type Merge request event review service.
 */
@Service
@Slf4j
@AllArgsConstructor
public class MergeRequestEventReviewService implements EventReviewer<MergeRequestEvent> {

    private final DingDingService dingDingService;
    private final DeepseekService deepseekService;


    @Override
    public void review(MergeRequestEvent mergeRequestEvent, String gitlabUrl, String gitlabToken, Date dtNow) {
        log.info("Merge Request Hook event received");
        // do nothing
    }

}
