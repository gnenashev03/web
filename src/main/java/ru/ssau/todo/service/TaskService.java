package ru.ssau.todo.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.entity.User;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.repository.TaskRepository;
import ru.ssau.todo.entity.dto.TaskDto;
import ru.ssau.todo.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService implements TaskServiceInterface {
    private final static int MAX_ACTIVE_TASKS = 10;
    private final static int MINUTES_TO_DELETE = 5;
    private final TaskRepository taskRepository;
    private UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
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
    private Task getTaskOrExcept(long id) throws TaskNotFoundException {
        Optional<Task> optional = taskRepository.findById(id);
        if (optional.isEmpty()) {
            throw new TaskNotFoundException(id);
        }
        return optional.get();
    }
    private TaskDto toDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setStatus(TaskStatus.valueOf(String.valueOf(task.getStatus())));
        taskDto.setCreatedBy(task.getCreatedBy().getId());
        taskDto.setCreatedAt(task.getCreatedAt());
        return taskDto;
    }
    @Override
    public TaskDto create(Task task) {
        checkActiveLimit(task);
        task.setCreatedAt(LocalDateTime.now());
        Task saved = taskRepository.save(task);
        return toDto(saved);
    }
    @Override
    public TaskDto createTask(TaskDto dto, String username) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setStatus(dto.getStatus());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        task.setCreatedBy(user);
        return create(task);
    }
    @Override
    public TaskDto update(Task task) throws TaskNotFoundException {
        Task existing = getTaskOrExcept(task.getId());
        checkActiveLimit(task);
        existing.setTitle(task.getTitle());
        existing.setStatus(task.getStatus());
        Task saved = taskRepository.save(existing);
        return toDto(saved);
    }

    @Override
    public void deleteById(long id) throws TaskNotFoundException {
        Task task = getTaskOrExcept(id);
        long minutes = ChronoUnit.MINUTES.between(task.getCreatedAt(), LocalDateTime.now());
        if (minutes < MINUTES_TO_DELETE) {
            throw new IllegalStateException("Cannot delete created task less than 5 minutes");
        }
        taskRepository.deleteById(id);
    }

    @Override
    public TaskDto findById(long id) throws TaskNotFoundException {
        Task task = getTaskOrExcept(id);
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

}