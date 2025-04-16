package com.nofirst.ai.code.review.service;

import com.nofirst.ai.code.review.config.PromptTemplatesConfiguration;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionModel;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeepseekService {

    private final PromptTemplatesConfiguration promptTemplatesConfiguration;
    private final DeepSeekClient deepSeekClient;

    public ChatCompletionResponse chat(String diffsText, String commitsText) {
        String systemPrompt = promptTemplatesConfiguration.getSystem();

        String userPrompt = promptTemplatesConfiguration.getUser();
        String replaced = userPrompt.replace("{diffs_text}", diffsText);
        String real = replaced.replace("{commits_text}", commitsText);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(ChatCompletionModel.DEEPSEEK_REASONER)
                .addSystemMessage(systemPrompt)
                .addUserMessage(real)
                .build();

        return deepSeekClient.chatCompletion(request).execute();
    }
}
