package com.nofirst.ai.code.review.service;

import com.nofirst.ai.code.review.config.PromptTemplatesConfiguration;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionModel;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.gitlab4j.api.models.CompareResults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DeepseekService {

    private final PromptTemplatesConfiguration promptTemplatesConfiguration;
    private final DeepSeekClient deepSeekClient;

    public ChatCompletionResponse chat(CompareResults compareResults) {
        log.info("Chat begin");

        String commitsText = getCommitsText(compareResults);
        String diffsText = getDiffsText(compareResults);

        String systemPrompt = promptTemplatesConfiguration.getSystem();

        String userPrompt = promptTemplatesConfiguration.getUser();
        String replaced = userPrompt.replace("{diffs_text}", diffsText);
        String real = replaced.replace("{commits_text}", commitsText);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(ChatCompletionModel.DEEPSEEK_REASONER)
                .addSystemMessage(systemPrompt)
                .addUserMessage(real)
                .build();

        ChatCompletionResponse chatResponse = deepSeekClient.chatCompletion(request).execute();

        log.info("Chat end,Chat completion response: {}", chatResponse);
        return chatResponse;
    }


    private static String getCommitsText(CompareResults compareResults) {
        List<String> collect = compareResults.getCommits().stream()
                .map(commit -> commit.getMessage().strip())
                .collect(Collectors.toList());

        return StringUtils.join(collect, ';');
    }

    private static String getDiffsText(CompareResults compareResults) {
        return compareResults.getDiffs().toString();
    }
}
