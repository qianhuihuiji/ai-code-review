package com.nofirst.ai.code.review.service.reviewer;

import com.nofirst.ai.code.review.model.chat.EvaluationReport;
import com.nofirst.ai.code.review.model.chat.Issue;
import com.nofirst.ai.code.review.model.chat.Result;
import com.nofirst.ai.code.review.model.chat.ReviewStatusEnum;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDAO;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDetailDAO;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDetailQuestionDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfo;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfoDetail;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfoDetailIssue;
import com.nofirst.ai.code.review.service.DeepseekService;
import com.nofirst.ai.code.review.service.DingDingService;
import com.nofirst.ai.code.review.util.EvaluationReportConvertUtil;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Comment;
import org.gitlab4j.api.models.CompareResults;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.webhook.EventCommit;
import org.gitlab4j.api.webhook.EventProject;
import org.gitlab4j.api.webhook.PushEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * The type Push event review service.
 */
@Service
@Slf4j
@AllArgsConstructor
public class PushEventReviewService implements EventReviewer<PushEvent> {

    private final DingDingService dingDingService;
    private final DeepseekService deepseekService;

    private final IReviewResultInfoDAO reviewResultInfoDAO;
    private final IReviewResultInfoDetailDAO reviewResultInfoDetailDAO;
    private final IReviewResultInfoDetailQuestionDAO detailQuestionDAO;


    @Override
    public void review(PushEvent pushEvent, ReviewConfigInfo reviewConfig, ReviewResultInfo reviewResultInfo) {
        log.info("Push Hook event begin review...");
        this.updateReviewResult(pushEvent, reviewResultInfo);

        CompareResults compareResults = this.getCompareResults(pushEvent, reviewConfig);

        ChatCompletionResponse chatResponse = deepseekService.chat(compareResults);

        EvaluationReport evaluationReport = EvaluationReportConvertUtil.convertFromChatContent(chatResponse.content());

        this.storeReviewResult(reviewResultInfo, evaluationReport);

        this.addPushNote(pushEvent, reviewConfig, evaluationReport.getMarkdownContent());

        this.sendDingDingMessage(pushEvent, reviewConfig, evaluationReport.getMarkdownContent());
    }

    public void reviewFile(ReviewConfigInfo reviewConfig) {
        // Create a GitLabApi instance to communicate with GitLab server
        try (GitLabApi gitLabApi = new GitLabApi(reviewConfig.getGitlabUrl(), reviewConfig.getGitlabToken())) {
//            List<TreeItem> tree = gitLabApi.getRepositoryApi().getTree(296L, "op-starter/src/main/java/com/hnup/osmp/op/starter/service/operator/OpHistoryService.java", "refs/heads/dev", true);
            RepositoryFile file = gitLabApi.getRepositoryFileApi().getFile(296L, "op-starter/src/main/java/com/hnup/osmp/op/starter/service/operator/OpPhaseTimeService.java", "refs/heads/dev", true);

            ChatCompletionResponse chatResponse = deepseekService.chat(file);

            EvaluationReport evaluationReport = EvaluationReportConvertUtil.convertFromChatContent(chatResponse.content());


            dingDingService.sendMessageWebhook("OpPhaseTimeService.java review result", evaluationReport.getMarkdownContent(), reviewConfig);
            log.info("Compare results: {}", 1);
        } catch (GitLabApiException e) {
            log.error("GitLab Repository API exception", e);
            throw new RuntimeException(e);
        }
    }

    private void updateReviewResult(PushEvent pushEvent, ReviewResultInfo reviewResultInfo) {
        reviewResultInfo.setObjectKind(pushEvent.getObjectKind());
        reviewResultInfo.setUserId(pushEvent.getUserId());
        reviewResultInfo.setUsername(pushEvent.getUserUsername());
        reviewResultInfo.setOriginInfo(pushEvent.toString());

        reviewResultInfoDAO.updateById(reviewResultInfo);
    }

    private void storeReviewResult(ReviewResultInfo reviewResultInfo, EvaluationReport evaluationReport) {
        Date dtNow = reviewResultInfo.getCreateTime();
        reviewResultInfo.setReviewResult(evaluationReport.getMarkdownContent());
        reviewResultInfo.setReviewScore(evaluationReport.getTotalScore());
        reviewResultInfo.setReviewStatus(ReviewStatusEnum.SUCCESS.getStatus());

        reviewResultInfoDAO.updateById(reviewResultInfo);

        for (Result result : evaluationReport.getResults()) {
            ReviewResultInfoDetail reviewResultInfoDetail = new ReviewResultInfoDetail();
            reviewResultInfoDetail.setResultId(reviewResultInfo.getId());
            reviewResultInfoDetail.setDimension(result.getDimension());
            reviewResultInfoDetail.setReviewScore(result.getScore());
            reviewResultInfoDetail.setFullScore(result.getFullScore());
            reviewResultInfoDetail.setCreateTime(dtNow);

            reviewResultInfoDetailDAO.save(reviewResultInfoDetail);

            for (Issue issue : result.getIssues()) {
                ReviewResultInfoDetailIssue detailQuestion = new ReviewResultInfoDetailIssue();
                detailQuestion.setDetailId(reviewResultInfoDetail.getId());
                detailQuestion.setFile(issue.getFile());
                detailQuestion.setTitle(issue.getTitle());
                detailQuestion.setDetail(issue.getDetail());
                detailQuestion.setSeverity(issue.getSeverity());
                detailQuestion.setAdvice(issue.getAdvice());
                detailQuestion.setCreateTime(dtNow);
                detailQuestionDAO.save(detailQuestion);
            }
        }
    }

    private void sendDingDingMessage(PushEvent pushEvent, ReviewConfigInfo reviewConfig, String markdownContent) {
        String title = getTitle(pushEvent);
        String content = getContent(pushEvent, markdownContent);

        dingDingService.sendMessageWebhook(title, content, reviewConfig);
    }

    @NotNull
    private static String getTitle(PushEvent pushEvent) {
        return pushEvent.getProject().getName() + " Push Event";
    }

    @NotNull
    private static String getContent(PushEvent pushEvent, String markdownContent) {
        StringBuilder sb = new StringBuilder();
        sb.append("### 🚀 ").append(pushEvent.getProject().getName())
                .append(": Push\n\n").append("#### 提交记录:\n");

        for (EventCommit commit : pushEvent.getCommits()) {
            String message = commit.getMessage();
            String author = commit.getAuthor().getName();
            Date timestamp = commit.getTimestamp();
            String url = commit.getUrl();
            sb.append("- **提交信息**: ").append(message).append("\n")
                    .append("- **提交者**: ").append(author).append("\n")
                    .append("- **时间**: ").append(timestamp).append("\n")
                    .append("- [查看提交详情](").append(url).append(")\n\n");
        }

        sb.append(markdownContent);
        return sb.toString();
    }

    /**
     * Add push note.
     *
     * @param pushEvent    the push event
     * @param reviewConfig the review config
     * @param content      the content
     */
    public void addPushNote(PushEvent pushEvent, ReviewConfigInfo reviewConfig, String content) {
        List<EventCommit> commits = pushEvent.getCommits();
        if (CollectionUtils.isEmpty(commits)) {
            log.info("Push Event has no commits");
            return;
        }
        try (GitLabApi gitLabApi = new GitLabApi(reviewConfig.getGitlabUrl(), reviewConfig.getGitlabToken())) {
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
     * @param pushEvent    the push event
     * @param reviewConfig the review config
     * @return the compare results
     */
    public CompareResults getCompareResults(PushEvent pushEvent, ReviewConfigInfo reviewConfig) {
        CompareResults compare;
        // Create a GitLabApi instance to communicate with GitLab server
        try (GitLabApi gitLabApi = new GitLabApi(reviewConfig.getGitlabUrl(), reviewConfig.getGitlabToken())) {
            String before = pushEvent.getBefore();
            String after = pushEvent.getAfter();
            EventProject project = pushEvent.getProject();

            compare = gitLabApi.getRepositoryApi().compare(project.getId(), before, after, true);
            log.info("Compare results: {}", compare);
            return compare;
        } catch (GitLabApiException e) {
            log.error("GitLab Repository API exception", e);
            throw new RuntimeException(e);
        }
    }
}
