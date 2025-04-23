package com.nofirst.ai.code.review.model.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MyPushEvent {

    @JsonProperty("object_kind")
    private String objectKind;

    @JsonProperty("event_name")
    private String eventName;

    @JsonProperty("before")
    private String before;

    @JsonProperty("after")
    private String after;

    @JsonProperty("ref")
    private String ref;

    @JsonProperty("ref_protected")
    private boolean refProtected;

    @JsonProperty("checkout_sha")
    private String checkoutSha;

    @JsonProperty("message")
    private String message;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_username")
    private String userUsername;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("user_avatar")
    private String userAvatar;

    @JsonProperty("project_id")
    private Long projectId;

    private MyProject project;

    @JsonProperty("commits")
    private List<MyCommit> commits;

    @JsonProperty("total_commits_count")
    private Integer totalCommitsCount;

    @JsonProperty("push_options")
    private Object pushOptions;

    @JsonProperty("repository")
    private MyRepository repository;

    public String getObjectKind() {
        return objectKind;
    }

    public void setObjectKind(String objectKind) {
        this.objectKind = objectKind;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public boolean isRefProtected() {
        return refProtected;
    }

    public void setRefProtected(boolean refProtected) {
        this.refProtected = refProtected;
    }

    public String getCheckoutSha() {
        return checkoutSha;
    }

    public void setCheckoutSha(String checkoutSha) {
        this.checkoutSha = checkoutSha;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public MyProject getProject() {
        return project;
    }

    public void setProject(MyProject project) {
        this.project = project;
    }

    public List<MyCommit> getCommits() {
        return commits;
    }

    public void setCommits(List<MyCommit> commits) {
        this.commits = commits;
    }

    public Integer getTotalCommitsCount() {
        return totalCommitsCount;
    }

    public void setTotalCommitsCount(Integer totalCommitsCount) {
        this.totalCommitsCount = totalCommitsCount;
    }

    public Object getPushOptions() {
        return pushOptions;
    }

    public void setPushOptions(Object pushOptions) {
        this.pushOptions = pushOptions;
    }

    public MyRepository getRepository() {
        return repository;
    }

    public void setRepository(MyRepository repository) {
        this.repository = repository;
    }
}
