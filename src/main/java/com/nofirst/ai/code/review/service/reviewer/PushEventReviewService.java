package com.nofirst.ai.code.review.service.reviewer;

import com.nofirst.ai.code.review.service.DeepseekService;
import com.nofirst.ai.code.review.service.DingDingService;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Comment;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.CompareResults;
import org.gitlab4j.api.webhook.EventCommit;
import org.gitlab4j.api.webhook.EventProject;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class PushEventReviewService implements EventReviewer<PushEvent> {

    private final DingDingService dingDingService;
    private final DeepseekService deepseekService;


    @Override
    public void review(PushEvent pushEvent, String gitlabUrl, String gitlabToken) {
        log.info("Push Hook event received");
        CompareResults compareResults = this.getCompareResults(pushEvent, gitlabUrl, gitlabToken);
        List<String> collect = compareResults.getCommits().stream()
                .map(commit -> commit.getMessage().strip()) // 获取并清理消息
                .collect(Collectors.toList());

        String commitsText = StringUtils.join(collect, ';');
        String changes = compareResults.getDiffs().toString();

        log.info("Chat begin");

        ChatCompletionResponse chat = deepseekService.chat(changes, commitsText);

        log.info("Chat end,Chat completion response: {}", chat);

        String content = extractMarkdown(chat.content());
        this.addPushNote(pushEvent, gitlabUrl, gitlabToken, content);

        StringBuilder sb = new StringBuilder();
        sb.append("### 🚀 ").append(pushEvent.getProject().getName())
                .append(": Push\n\n").append("#### 提交记录:\n");

        for (Commit commit : compareResults.getCommits()) {
            String message = commit.getMessage().strip();
            String author = commit.getAuthorName();
            Date timestamp = commit.getTimestamp();
            String url = commit.getUrl();
            sb.append("- **提交信息**: ").append(message).append("\n")
                    .append("- **提交者**: ").append(author).append("\n")
                    .append("- **时间**: ").append(timestamp).append("\n")
                    .append("- [查看提交详情](").append(url).append(")\n\n");

        }

        sb.append("#### AI Code Review 结果: \n").append(content);

        String title = pushEvent.getProject().getName() + " Push Event";
        dingDingService.sendMessageWebhook(title, sb.toString());
    }

    public static String extractMarkdown(String reviewResult) {
        String startMarker = "```markdown";
        String endMarker = "```";

        if (reviewResult.startsWith(startMarker) && reviewResult.endsWith(endMarker)) {
            int start = startMarker.length();
            int end = reviewResult.length() - endMarker.length();
            return reviewResult.substring(start, end).trim();
        }
        return reviewResult;
    }

    public void addPushNote(PushEvent pushEvent, String gitlabUrl, String gitlabToken, String content) {
        List<EventCommit> commits = pushEvent.getCommits();
        if (CollectionUtils.isEmpty(commits)) {
            log.info("Push Event has no commits");
            return;
        }
        try (GitLabApi gitLabApi = new GitLabApi(gitlabUrl, gitlabToken)) {
            EventCommit lastCommit = commits.get(commits.size() - 1);
            EventProject project = pushEvent.getProject();

            Comment comment = gitLabApi.getCommitsApi().addComment(project.getId(), lastCommit.getId(), content);
            log.info("comment results: {}", comment);
        } catch (GitLabApiException e) {
            log.error("GitLab Commits API exception", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets compare results.
     *
     * @param pushEvent   the push event
     * @param gitlabUrl   the gitlab url
     * @param gitlabToken the gitlab token
     * @return the compare results
     */
    public CompareResults getCompareResults(PushEvent pushEvent, String gitlabUrl, String gitlabToken) {
        CompareResults compare;
        // Create a GitLabApi instance to communicate with GitLab server
        try (GitLabApi gitLabApi = new GitLabApi(gitlabUrl, gitlabToken)) {
            String before = pushEvent.getBefore();
            String after = pushEvent.getAfter();
            EventProject project = pushEvent.getProject();

            compare = gitLabApi.getRepositoryApi().compare(project.getId(), before, after, project.getId(), true);
            log.info("Compare results: {}", compare);
            return compare;
        } catch (GitLabApiException e) {
            log.error("GitLab Repository API exception", e);
            throw new RuntimeException(e);
        }
    }
}
