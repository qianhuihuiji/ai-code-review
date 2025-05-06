package com.nofirst.ai.code.review.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import com.taobao.api.ApiException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@AllArgsConstructor
public class DingDingService {

    /**
     * Send message webhook.
     *
     * @param title   the title
     * @param content the content
     */
    public void sendMessageWebhook(String title, String content, ReviewConfigInfo reviewConfig) {
        if (StringUtils.hasText(reviewConfig.getDingWebhookUrl())) {
            DingTalkClient client = new DefaultDingTalkClient(reviewConfig.getDingWebhookUrl());
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            request.setMsgtype("markdown");
            OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
            markdown.setTitle(title);
            markdown.setText(content);
            request.setMarkdown(markdown);

            try {
                OapiRobotSendResponse response = client.execute(request);
                log.info("send dingTalkClient response: {}", response);
            } catch (ApiException e) {
                log.error("send dingTalkClient error: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            log.warn("ding webhook url is empty");
        }
    }
}
