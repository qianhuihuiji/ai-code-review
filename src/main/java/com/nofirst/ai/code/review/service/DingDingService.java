package com.nofirst.ai.code.review.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.nofirst.ai.code.review.config.DingTalkConfiguration;
import com.taobao.api.ApiException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class DingDingService {


    private final DingTalkConfiguration dingTalkConfiguration;

    /**
     * Send message webhook.
     *
     * @param title   the title
     * @param content the content
     */
    public void sendMessageWebhook(String title, String content) {
        DingTalkClient client = new DefaultDingTalkClient(dingTalkConfiguration.getWebhook());
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
    }
}
