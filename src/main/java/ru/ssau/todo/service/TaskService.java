package ru.ssau.todo.service;

import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.repository.TaskRepository;
import ru.ssau.todo.entity.dto.TaskDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService implements TaskServiceInterface {
    private final static int MAX_ACTIVE_TASKS = 10;
    private final static int MINUTES_TO_DELETE = 5;
    private final TaskRepository taskRepository;
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    private boolean isActive(Task task) {
        return task.getStatus() == TaskStatus.OPEN || task.getStatus() == TaskStatus.IN_PROGRESS;
    }
    private void checkActiveLimit(Task task) {
        long activeCount = taskRepository.countActiveTasksByUserId(task.getCreatedBy().getId());
        if (isActive(task) && activeCount >= MAX_ACTIVE_TASKS) {
            throw new IllegalStateException("User already has 10 active tasks");
        }
    }

    @Override
    public TaskDto create(Task task) {
        checkActiveLimit(task);
        task.setCreatedAt(LocalDateTime.now());
        Task saved = taskRepository.save(task);
        return toDto(saved);
    }

    @Override
    public TaskDto update(Task task) throws TaskNotFoundException {
        Task existing = taskRepository.findById(task.getId())
                .orElseThrow(() -> new TaskNotFoundException(task.getId()));
        checkActiveLimit(task);

        existing.setTitle(task.getTitle());
        existing.setStatus(task.getStatus());
        Task saved = taskRepository.save(existing);
        return toDto(saved);
    }

    @Override
    public void deleteById(long id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        long minutes = ChronoUnit.MINUTES.between(task.getCreatedAt(), LocalDateTime.now());
        if (minutes < MINUTES_TO_DELETE) {
            throw new IllegalStateException("Cannot delete task younger than 5 minutes");
        }
        taskRepository.deleteById(id);
    }

    @Override
    public TaskDto findById(long id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return toDto(task);
    }

    @Override
    public List<TaskDto> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        List<Task> tasks = taskRepository.findAll(from, to, userId);
        List<TaskDto> dtos = new ArrayList<>();
        for (Task t : tasks) {
            dtos.add(toDto(t));
        }
        return dtos;
    }

    @Override
    public long countActiveTasksByUserId(long userId) {
        return taskRepository.countActiveTasksByUserId(userId);
    }

    private TaskDto toDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getStatus(),
                task.getCreatedBy().getId(),
                task.getCreatedAt()
        );
    }
}