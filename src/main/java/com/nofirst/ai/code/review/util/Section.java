package com.nofirst.ai.code.review.util;

import java.util.List;

public class Section {
    private String title;
    private List<ContentItem> contents;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ContentItem> getContents() {
        return contents;
    }

    public void setContents(List<ContentItem> contents) {
        this.contents = contents;
    }
}