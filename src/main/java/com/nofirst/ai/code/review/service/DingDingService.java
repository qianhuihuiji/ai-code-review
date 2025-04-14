package com.nofirst.ai.code.review.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DingDingService {

    public void sendMessageWebhook(String title, String content) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=68c7177335e14fe4437e3474ce0598e6920a54e27d6c445e0d0594106d8cbbc2");
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
