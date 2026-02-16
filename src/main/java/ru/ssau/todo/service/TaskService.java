package ru.ssau.todo.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
@Service
@Profile("jdbc")
public class TaskService implements TaskServiceInterface {
    private final static int countTasks=10;
    private final static int minutesToTask=5;
    private final TaskRepository taskRepository;
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    private boolean isActive(Task task){
        return task.getStatus() == TaskStatus.OPEN || task.getStatus() == TaskStatus.IN_PROGRESS;
    }
    //проверка на наличие 10 и более задач
    private void checkActiveLimit(Task task, Task existingTask) {
        long activeCount = taskRepository.countActiveTasksByUserId(
                existingTask != null ? existingTask.getCreatedBy() : task.getCreatedBy());
        if (existingTask != null && isActive(existingTask)) {
            activeCount--;
        }
        if (isActive(task) && activeCount >= countTasks) {
            throw new IllegalStateException("User already has 10 active tasks");
        }
    }

    @Override
    public Task create(Task task) {
        checkActiveLimit(task, null);
        return taskRepository.create(task);
    }
    @Override
    public void deleteById(long id) throws TaskNotFoundException {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            throw new TaskNotFoundException(id);
        }
        Task task = taskOpt.get();
        long minutes = ChronoUnit.MINUTES.between(
                task.getCreatedAt(), LocalDateTime.now()
        );
        if (minutes < minutesToTask) {
            throw new IllegalStateException("Cannot delete task younger than 5 minutes");
        }
        taskRepository.deleteById(id);
    }
    @Override
    public void update(Task task) throws TaskNotFoundException {
        Optional<Task> taskExist = taskRepository.findById(task.getId());
        if (taskExist.isEmpty()) {
            throw new TaskNotFoundException(task.getId());
        }
        Task existingTask = taskExist.get();
        checkActiveLimit(task, existingTask);
        taskRepository.update(existingTask);
    }
    @Override
    public Optional<Task> findById(long id) {
        return taskRepository.findById(id);
    }
    @Override
    public java.util.List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        return taskRepository.findAll(from, to, userId);
    }
    @Override
    public long countActiveTasksByUserId(long userId) {
        return taskRepository.countActiveTasksByUserId(userId);
    }
}
