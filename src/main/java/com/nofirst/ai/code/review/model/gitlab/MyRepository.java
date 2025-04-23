package com.nofirst.ai.code.review.model.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MyRepository {

    private String name;

    private String url;

    private String description;

    private String homepage;

    @JsonProperty("git_http_url")
    private String gitHttpUrl;

    @JsonProperty("git_ssh_url")
    private String gitSshUrl;

    @JsonProperty("visibility_level")
    private int visibilityLevel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getGitHttpUrl() {
        return gitHttpUrl;
    }

    public void setGitHttpUrl(String gitHttpUrl) {
        this.gitHttpUrl = gitHttpUrl;
    }

    public String getGitSshUrl() {
        return gitSshUrl;
    }

    public void setGitSshUrl(String gitSshUrl) {
        this.gitSshUrl = gitSshUrl;
    }

    public int getVisibilityLevel() {
        return visibilityLevel;
    }

    public void setVisibilityLevel(int visibilityLevel) {
        this.visibilityLevel = visibilityLevel;
    }
}
