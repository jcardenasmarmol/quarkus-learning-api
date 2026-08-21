package com.jcard.quarkus;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TaskRepository {

    private final List<Task> tasks = new ArrayList<>();

    private Long nextId = 1L;

    public TaskRepository() {
        tasks.add(new Task(nextId++, "Learn Quarkus", false));
        tasks.add(new Task(nextId++, "Learn REST", false));
        tasks.add(new Task(nextId++, "Build an API", true));
    }

    public List<Task> findAll() {
        return tasks;
    }

    public Task findById(Long id) {
        for (Task task : tasks) {
            if (task.getId().equals(id)) {
                return task;
            }
        }

        return null;
    }

    public Task save(Task task) {
        task.setId(nextId++);
        tasks.add(task);

        return task;
    }

    public boolean deleteById(Long id) {
        return tasks.removeIf(task -> task.getId().equals(id));
    }
}