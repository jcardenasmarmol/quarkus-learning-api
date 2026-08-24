package com.jcard.quarkus;

import jakarta.validation.constraints.NotBlank;

public class TaskCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private boolean completed;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}