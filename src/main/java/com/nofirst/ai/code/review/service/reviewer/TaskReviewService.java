package com.nofirst.ai.code.review.service.reviewer;

import com.nofirst.ai.code.review.model.chat.EvaluationReport;
import com.nofirst.ai.code.review.model.enums.TaskReviewStatusEnum;
import com.nofirst.ai.code.review.repository.dao.IReviewFileTaskDAO;
import com.nofirst.ai.code.review.repository.dao.IReviewProjectTaskDAO;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDetailDAO;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDetailQuestionDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import com.nofirst.ai.code.review.repository.entity.ReviewFileTask;
import com.nofirst.ai.code.review.repository.entity.ReviewProjectTask;
import com.nofirst.ai.code.review.service.DeepseekService;
import com.nofirst.ai.code.review.service.DingDingService;
import com.nofirst.ai.code.review.util.EvaluationReportConvertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.models.TreeItem;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Push event review service.
 */
@Service
@Slf4j
@AllArgsConstructor
public class TaskReviewService {

    private final DingDingService dingDingService;
    private final DeepseekService deepseekService;

    private final IReviewFileTaskDAO reviewFileTaskDAO;
    private final IReviewProjectTaskDAO reviewProjectTaskDAO;
    private final IReviewResultInfoDetailDAO reviewResultInfoDetailDAO;
    private final IReviewResultInfoDetailQuestionDAO detailQuestionDAO;

    public void reviewFile(ReviewConfigInfo reviewConfig, String filePath, String ref) {
        // 创建任务记录
        ReviewFileTask reviewFileTask = initFileTask(reviewConfig, filePath, ref, null);

        try (GitLabApi gitLabApi = new GitLabApi(reviewConfig.getGitlabUrl(), reviewConfig.getGitlabToken())) {
            RepositoryFile repositoryFile = fetchRepositoryFile(gitLabApi, reviewConfig, filePath, ref);
            String chat = deepseekService.chat(repositoryFile);

            EvaluationReport evaluationReport = EvaluationReportConvertUtil.convertFromChatContent(chat);

            String fileName = extraFileName(filePath);

            reviewFileTask.setReviewResult(evaluationReport.getMarkdownContent());
            reviewFileTask.setReviewScore(evaluationReport.getTotalScore());
            reviewFileTask.setReviewStatus(TaskReviewStatusEnum.SUCCESS.getStatus());
            reviewFileTaskDAO.updateById(reviewFileTask);

            dingDingService.sendMessageWebhook(fileName + " review result", evaluationReport.getMarkdownContent(), reviewConfig);
        } catch (Exception e) {
            reviewFileTask.setReviewStatus(TaskReviewStatusEnum.FAIL.getStatus());
            reviewFileTaskDAO.updateById(reviewFileTask);

            throw new RuntimeException(e);
        }
    }

    private ReviewProjectTask initProjectTask(ReviewConfigInfo reviewConfig, String filePath, String ref) {
        Date now = new Date();
        ReviewProjectTask reviewProjectTask = new ReviewProjectTask();
        reviewProjectTask.setCreateTime(now);
        reviewProjectTask.setConfigId(reviewConfig.getId());
        reviewProjectTask.setProjectName(reviewConfig.getProjectName());
        reviewProjectTask.setReviewFileCount(0);
        reviewProjectTask.setTotalFileCount(0);
        reviewProjectTask.setPath(filePath);
        reviewProjectTask.setRef(ref);
        reviewProjectTaskDAO.save(reviewProjectTask);

        return reviewProjectTask;
    }

    private ReviewFileTask initFileTask(ReviewConfigInfo reviewConfig, String filePath, String ref, Long taskId) {
        Date now = new Date();
        ReviewFileTask reviewFileTask = new ReviewFileTask();
        reviewFileTask.setTaskId(taskId);
        reviewFileTask.setCreateTime(now);
        reviewFileTask.setConfigId(reviewConfig.getId());
        reviewFileTask.setProjectName(reviewConfig.getProjectName());
        reviewFileTask.setReviewStatus(TaskReviewStatusEnum.NOT_STARTED.getStatus());
        reviewFileTask.setFilePath(filePath);
        reviewFileTask.setRef(ref);
        reviewFileTaskDAO.save(reviewFileTask);

        return reviewFileTask;
    }

    private RepositoryFile fetchRepositoryFile(GitLabApi gitLabApi, ReviewConfigInfo reviewConfig, String filePath, String ref) {
        try {
            return gitLabApi.getRepositoryFileApi().getFile(reviewConfig.getProjectId(), filePath, ref, true);
        } catch (GitLabApiException e) {
            log.error("GitLab Repository API exception", e);
            throw new RuntimeException(e);
        }
    }

    private String extraFileName(String path) {
        Path pathObj = Paths.get(path);
        return pathObj.getFileName().toString();
    }

    public void reviewProject(ReviewConfigInfo reviewConfig, String path, String ref) {
        ReviewProjectTask reviewProjectTask = initProjectTask(reviewConfig, path, ref);

        try (GitLabApi gitLabApi = new GitLabApi(reviewConfig.getGitlabUrl(), reviewConfig.getGitlabToken())) {
            List<TreeItem> fileTree = gitLabApi.getRepositoryApi().getTree(reviewConfig.getProjectId(), path, ref, true);
            // 过滤得到文件类型的 item
            List<TreeItem> realFileTree = fileTree.stream()
                    .filter(t -> TreeItem.Type.BLOB.equals(t.getType()) && t.getPath().endsWith(".java")).collect(Collectors.toList());

            reviewProjectTask.setTotalFileCount(realFileTree.size());
            reviewProjectTaskDAO.updateById(reviewProjectTask);

            for (TreeItem treeItem : realFileTree) {
                // 创建任务记录
                String filePath = treeItem.getPath();
                ReviewFileTask reviewFileTask = initFileTask(reviewConfig, filePath, ref, reviewProjectTask.getId());
                try {
                    RepositoryFile repositoryFile = fetchRepositoryFile(gitLabApi, reviewConfig, filePath, ref);
                    String chat = deepseekService.chat(repositoryFile);

                    EvaluationReport evaluationReport = EvaluationReportConvertUtil.convertFromChatContent(chat);
                    reviewFileTask.setReviewResult(evaluationReport.getMarkdownContent());
                    reviewFileTask.setReviewScore(evaluationReport.getTotalScore());
                    reviewFileTask.setReviewStatus(TaskReviewStatusEnum.SUCCESS.getStatus());
                    reviewFileTaskDAO.updateById(reviewFileTask);

                    reviewProjectTask.setReviewFileCount(reviewProjectTask.getReviewFileCount() + 1);
                    reviewProjectTaskDAO.updateById(reviewProjectTask);
                } catch (Exception e) {
                    reviewFileTask.setReviewStatus(TaskReviewStatusEnum.FAIL.getStatus());
                    reviewFileTaskDAO.updateById(reviewFileTask);

                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
