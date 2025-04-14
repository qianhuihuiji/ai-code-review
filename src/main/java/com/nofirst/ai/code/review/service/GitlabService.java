package com.nofirst.ai.code.review.service;

import org.apache.tomcat.util.buf.StringUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.CompareResults;
import org.gitlab4j.api.webhook.EventProject;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitlabService {

    public CompareResults getCompareResults(PushEvent pushEvent, String gitlabUrl, String gitlabToken) throws GitLabApiException {
        // Create a GitLabApi instance to communicate with your GitLab server
        GitLabApi gitLabApi = new GitLabApi(gitlabUrl, gitlabToken);

        String before = pushEvent.getBefore();
        String after = pushEvent.getAfter();
        Long projectId = pushEvent.getProjectId();
        EventProject project = pushEvent.getProject();

// Get the list of projects your account has access to
        CompareResults compare = gitLabApi.getRepositoryApi().compare(project.getId(), before, after,project.getId(), true);

        return compare;
    }
}
