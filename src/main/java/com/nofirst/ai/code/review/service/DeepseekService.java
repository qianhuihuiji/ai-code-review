package com.nofirst.ai.code.review.service;

import com.nofirst.ai.code.review.configuration.PromptTemplatesConfiguration;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionModel;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.ResponseFormatType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeepseekService {

    @Autowired
    private PromptTemplatesConfiguration promptTemplatesConfiguration;

    @Autowired
    private DeepSeekClient deepSeekClient;

    public ChatCompletionResponse chat(String changes, String commitsText) {
        String systemPrompt = promptTemplatesConfiguration.getSystem();

        String userPrompt = promptTemplatesConfiguration.getUser();
        String replaced = userPrompt.replace("{diffs_text}", changes);
        String real = replaced.replace("{commits_text}", commitsText);


        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(ChatCompletionModel.DEEPSEEK_CHAT)
                .addSystemMessage(systemPrompt)
                .addUserMessage(real)
                .build();

        return deepSeekClient.chatCompletion(request).execute();
    }
}
