package ru.ssau.todo.service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.dto.TaskDto;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskServiceInterface {
    TaskDto create(Task task);
    TaskDto findById(long id) throws TaskNotFoundException;
    List<TaskDto> findAll(LocalDateTime from, LocalDateTime to, long userId);
    TaskDto update(Task task) throws TaskNotFoundException;
    void deleteById(long id) throws TaskNotFoundException;
    long countActiveTasksByUserId(long userId);
}
