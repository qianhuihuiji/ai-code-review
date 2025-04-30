package com.nofirst.ai.code.review.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.nofirst.ai.code.review.config.PromptTemplatesConfiguration;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionChoice;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.Delta;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.CompareResults;
import org.gitlab4j.api.models.RepositoryFile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DeepseekService {

    private final PromptTemplatesConfiguration promptTemplatesConfiguration;
    private final DeepSeekClient deepSeekClient;

    public String chat(CompareResults compareResults) {
        log.info("Chat begin");
        StringBuffer sb = new StringBuffer();

        String commitsText = getCommitsText(compareResults);
        String diffsText = getDiffsText(compareResults);

        String systemPrompt = promptTemplatesConfiguration.getSystem();

        String userPrompt = promptTemplatesConfiguration.getUser();
        String replaced = userPrompt.replace("{diffs_text}", diffsText);
        String real = replaced.replace("{commits_text}", commitsText);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .addSystemMessage(systemPrompt)
                .addUserMessage(real)
                .build();

        Flux<ChatCompletionResponse> chatCompletionResponseFlux = deepSeekClient.chatFluxCompletion(request)
                .doOnSubscribe(s -> log.info("开始流式请求"))
                .doOnNext(response -> {
                    if (CollectionUtils.isNotEmpty(response.choices())) {
                        ChatCompletionChoice chatCompletionChoice = response.choices().get(0);
                        Delta delta = chatCompletionChoice.delta();

                        String content = delta.content() != null ? delta.content() : "";
                        sb.append(content);
                    }
                }).doOnError(e -> log.error("chat error:{}", e.getMessage()));

        //阻塞等待流完成
        chatCompletionResponseFlux.blockLast();

        return sb.toString();
    }

    public String chat(RepositoryFile repositoryFile) {
        log.info("Chat begin");

        StringBuffer sb = new StringBuffer();

        String systemPrompt = promptTemplatesConfiguration.getSystem();

        String filePrompt = promptTemplatesConfiguration.getFile();
        String real = filePrompt.replace("{code_text}", repositoryFile.getContent());

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .addSystemMessage(systemPrompt)
                .addUserMessage(real)
                .build();


        Flux<ChatCompletionResponse> chatCompletionResponseFlux = deepSeekClient.chatFluxCompletion(request)
                .doOnSubscribe(s -> log.info("开始流式请求"))
                .doOnNext(response -> {
                    if (CollectionUtils.isNotEmpty(response.choices())) {
                        ChatCompletionChoice chatCompletionChoice = response.choices().get(0);
                        Delta delta = chatCompletionChoice.delta();

                        String content = delta.content() != null ? delta.content() : "";
                        sb.append(content);
                    }
                }).doOnError(e -> log.error("chat error:{}", e.getMessage()));

        //阻塞等待流完成
        chatCompletionResponseFlux.blockLast();

        return sb.toString();
    }


    private static String getCommitsText(CompareResults compareResults) {
        List<String> collect = compareResults.getCommits().stream()
                .map(Commit::getMessage)
                .collect(Collectors.toList());

        return StringUtils.join(collect, ';');
    }

    private static String getDiffsText(CompareResults compareResults) {
        return compareResults.getDiffs().toString();
    }
}
