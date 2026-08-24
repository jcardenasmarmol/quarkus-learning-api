package com.jcard.quarkus;

public class TaskResponse {

    private Long id;
    private String title;
    private boolean completed;

    public TaskResponse(Long id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.completed = task.isCompleted();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }
}