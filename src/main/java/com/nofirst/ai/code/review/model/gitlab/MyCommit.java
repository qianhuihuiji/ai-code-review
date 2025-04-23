package com.nofirst.ai.code.review.model.gitlab;

import java.util.Date;
import java.util.List;

public class MyCommit {

    private String id;

    private String message;

    private String title;

    private Date timestamp;

    private String url;

    private MyAuthor author;

    private List<String> added;

    private List<String> modified;

    private List<String> removed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MyAuthor getAuthor() {
        return author;
    }

    public void setAuthor(MyAuthor author) {
        this.author = author;
    }

    public List<String> getAdded() {
        return added;
    }

    public void setAdded(List<String> added) {
        this.added = added;
    }

    public List<String> getModified() {
        return modified;
    }

    public void setModified(List<String> modified) {
        this.modified = modified;
    }

    public List<String> getRemoved() {
        return removed;
    }

    public void setRemoved(List<String> removed) {
        this.removed = removed;
    }
}