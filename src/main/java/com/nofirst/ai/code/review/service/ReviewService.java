package com.nofirst.ai.code.review.service;

import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionModel;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.ResponseFormatType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {


    @Autowired
    private DeepSeekClient deepSeekClient;

    public void review() {
        String systemPrompt = "   The user will provide some exam text. Please parse the \"question\" and \"answer\" and output them in JSON format.\n" +
                "            \n" +
                "            EXAMPLE INPUT:\n" +
                "            Which is the highest mountain in the world? Mount Everest.\n" +
                "            \n" +
                "            EXAMPLE JSON OUTPUT:\n" +
                "            {\n" +
                "                \"question\": \"Which is the highest mountain in the world?\",\n" +
                "                \"answer\": \"Mount Everest\"\n" +
                "            }";



        String userPrompt = "Which is the longest river in the world? The Nile River.";


        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(ChatCompletionModel.DEEPSEEK_CHAT)
                .addSystemMessage(systemPrompt)
                .addUserMessage(userPrompt)
                .responseFormat(ResponseFormatType.JSON_OBJECT)
                .build();

        ChatCompletionResponse completionResponse = deepSeekClient.chatCompletion(request).execute();

    }
}
