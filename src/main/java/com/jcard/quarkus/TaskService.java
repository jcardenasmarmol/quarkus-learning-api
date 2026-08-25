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

    public Task getTaskById(Long id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id);

        if (task == null) {
            throw new TaskNotFoundException(id);
        }
        return task;
    }

    @Transactional
    public Task addTask(Task task) {
        taskRepository.persist(task);
        return task;
    }

    @Transactional
    public Task updateTask(Long id, Task updatedTask) throws TaskNotFoundException {

        Task task = taskRepository.findById(id);

        if (task == null) {
            throw new TaskNotFoundException(id);
        }

        task.setTitle(updatedTask.getTitle());
        task.setCompleted(updatedTask.isCompleted());

        return task;
    }

    @Transactional
    public boolean deleteTask(Long id) throws TaskNotFoundException {
        boolean deleted = taskRepository.deleteById(id);

        if (!deleted) throw new TaskNotFoundException(id);
        else return true;
    }
}
