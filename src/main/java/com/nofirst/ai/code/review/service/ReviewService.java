package com.nofirst.ai.code.review.service;

import com.taobao.api.ApiException;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.CompareResults;
import org.gitlab4j.api.webhook.EventCommit;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {

    @Autowired
    private DingDingService dingDingService;

    @Autowired
    private DeepseekService deepseekService;

    @Autowired
    private GitlabService gitlabService;

    public void review(PushEvent pushEvent, String gitlabUrl, String gitlabToken) throws ApiException, GitLabApiException {
        log.info("Push Hook event received");
        CompareResults compareResults = gitlabService.getCompareResults(pushEvent, gitlabUrl, gitlabToken);
        List<String> collect = compareResults.getCommits().stream()
                .map(commit -> commit.getMessage().strip()) // 获取并清理消息
                .collect(Collectors.toList());

        String commitsText = StringUtils.join(collect, ';');
        String changes = compareResults.getDiffs().toString();
        ChatCompletionResponse chat = deepseekService.chat(changes, commitsText);

        log.info("Chat completion response: {}", chat);

        String title = "### 🚀 " + pushEvent.getProject().getName() + ": Push\n\n";
        title += "#### 提交记录:\n";

        dingDingService.sendMessageWebhook("text", chat.choices().get(0).message().content());

    }


    public static String slugifyUrl(String originalUrl) {
        // 1. 移除URL协议头（http:// 或 https://）
        String processed = originalUrl.replaceAll("^https?://", "");

        // 2. 将所有非字母数字字符替换为下划线
        processed = processed.replaceAll("[^a-zA-Z0-9]", "_");

        // 3. 移除末尾可能存在的连续下划线
        processed = processed.replaceAll("_+$", "");

        return processed;
    }
}
