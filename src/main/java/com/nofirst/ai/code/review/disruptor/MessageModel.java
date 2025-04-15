package com.nofirst.ai.code.review.disruptor;

import lombok.Data;
import org.gitlab4j.api.webhook.Event;

@Data
public class MessageModel {

    private Event event;

    private String gitlabUrl;

    private String gitlabToken;
}
