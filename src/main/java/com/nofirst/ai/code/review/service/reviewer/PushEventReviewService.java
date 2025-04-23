package com.nofirst.ai.code.review.service.reviewer;

import com.nofirst.ai.code.review.model.chat.EvaluationReport;
import com.nofirst.ai.code.review.model.chat.Question;
import com.nofirst.ai.code.review.model.chat.Result;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDAO;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDetailAdviceDAO;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDetailDAO;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDetailQuestionDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfo;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfoDetail;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfoDetailAdvice;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfoDetailQuestion;
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
import org.gitlab4j.api.webhook.EventCommit;
import org.gitlab4j.api.webhook.EventProject;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PushEventReviewService implements EventReviewer<PushEvent> {

    private final DingDingService dingDingService;
    private final DeepseekService deepseekService;

    private final IReviewResultInfoDAO reviewResultInfoDAO;
    private final IReviewResultInfoDetailDAO reviewResultInfoDetailDAO;
    private final IReviewResultInfoDetailQuestionDAO detailQuestionDAO;
    private final IReviewResultInfoDetailAdviceDAO adviceDAO;


    @Override
    public void review(PushEvent pushEvent, String gitlabUrl, String gitlabToken, Date dtNow) {
        log.info("Push Hook event received");

        CompareResults compareResults = this.getCompareResults(pushEvent, gitlabUrl, gitlabToken);

        ChatCompletionResponse chatResponse = deepseekService.chat(compareResults);

        EvaluationReport evaluationReport = EvaluationReportConvertUtil.convertFromChatContent(chatResponse.content());

        this.storeReviewResult(pushEvent, dtNow, evaluationReport);

        this.addPushNote(pushEvent, gitlabUrl, gitlabToken, evaluationReport.getMarkdownContent());

        this.sendDingDingMessage(pushEvent, compareResults, evaluationReport.getMarkdownContent());
    }

    private void storeReviewResult(PushEvent pushEvent, Date dtNow, EvaluationReport evaluationReport) {
        ReviewResultInfo reviewResultInfo = new ReviewResultInfo();
        reviewResultInfo.setObjectKind(pushEvent.getObjectKind());
        reviewResultInfo.setUserId(pushEvent.getUserId());
        reviewResultInfo.setUsername(pushEvent.getUserUsername());
        reviewResultInfo.setOriginInfo(pushEvent.toString());
        reviewResultInfo.setReviewResult(evaluationReport.getMarkdownContent());
        reviewResultInfo.setReviewScore(evaluationReport.getTotalScore());
        reviewResultInfo.setCreateTime(dtNow);
        reviewResultInfoDAO.save(reviewResultInfo);

        for (Result result : evaluationReport.getResults()) {
            ReviewResultInfoDetail reviewResultInfoDetail = new ReviewResultInfoDetail();
            reviewResultInfoDetail.setResultId(reviewResultInfo.getId());
            reviewResultInfoDetail.setType(result.getType());
            reviewResultInfoDetail.setReviewScore(result.getScore());
            reviewResultInfoDetail.setCreateTime(dtNow);

            reviewResultInfoDetailDAO.save(reviewResultInfoDetail);

            for (Question question : result.getQuestions()) {
                ReviewResultInfoDetailQuestion detailQuestion = new ReviewResultInfoDetailQuestion();
                detailQuestion.setDetailId(reviewResultInfoDetail.getId());
                detailQuestion.setTitle(question.getTitle());
                detailQuestion.setContent(question.getContent());
                detailQuestion.setCreateTime(dtNow);
                detailQuestionDAO.save(detailQuestion);
            }
            for (String advice : result.getAdvices()) {
                ReviewResultInfoDetailAdvice detailAdvice = new ReviewResultInfoDetailAdvice();
                detailAdvice.setDetailId(reviewResultInfoDetail.getId());
                detailAdvice.setContent(advice);
                detailAdvice.setCreateTime(dtNow);
                adviceDAO.save(detailAdvice);
            }
        }
    }

    private void sendDingDingMessage(PushEvent pushEvent, CompareResults compareResults, String markdownContent) {
        StringBuilder sb = getStringBuilder(pushEvent, compareResults, markdownContent);

        String title = pushEvent.getProject().getName() + " Push Event";
        dingDingService.sendMessageWebhook(title, sb.toString());
    }

    private StringBuilder getStringBuilder(PushEvent pushEvent, CompareResults compareResults, String markdownContent) {
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
        return sb;
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

            compare = gitLabApi.getRepositoryApi().compare(project.getId(), before, after, true);
            log.info("Compare results: {}", compare);
            return compare;
        } catch (GitLabApiException e) {
            log.error("GitLab Repository API exception", e);
            throw new RuntimeException(e);
        }
    }
}
