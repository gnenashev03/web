package ru.ssau.todo.service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public interface TaskServiceInterface {
    Task create(Task task);
    Optional<Task> findById(long id);
    List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId);
    void update(Task task) throws TaskNotFoundException;
    void deleteById(long id) throws TaskNotFoundException;
    long countActiveTasksByUserId(long userId);
}
