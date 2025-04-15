package com.nofirst.ai.code.review.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.nofirst.ai.code.review.config.DingTalkConfiguration;
import com.taobao.api.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DingDingService {

    @Autowired
    private DingTalkConfiguration dingTalkConfiguration;

    public void sendMessageWebhook(String title, String content) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(dingTalkConfiguration.getWebhook());
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(title);
        markdown.setText(content);
        request.setMarkdown(markdown);

        OapiRobotSendResponse response = client.execute(request);
        System.out.println(response.getBody());
    }
}
