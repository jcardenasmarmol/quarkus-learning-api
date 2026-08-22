package com.jcard.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TaskService {

    @Inject
    TaskRepository taskRepository;

    public List<Task> getTasks() {
        return taskRepository.listAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional
    public Task addTask(Task task) {
        taskRepository.persist(task);
        return task;
    }

    @Transactional
    public Task updateTask(Long id, Task updatedTask) {

        Task task = taskRepository.findById(id);

        if (task == null) {
            return null;
        }

        task.setTitle(updatedTask.getTitle());
        task.setCompleted(updatedTask.isCompleted());

        return task;
    }

    @Transactional
    public boolean deleteTask(Long id) {
        return taskRepository.deleteById(id);
    }
}
