package com.nofirst.ai.code.review.disruptor;

import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import lombok.Data;
import org.gitlab4j.api.webhook.Event;

@Data
public class MessageModel {

    private Event event;

    private ReviewConfigInfo reviewConfigInfo;
}
