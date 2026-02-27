package ru.ssau.todo.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.dto.TaskDto;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.service.TaskServiceInterface;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskServiceInterface service;

    public TaskController(TaskServiceInterface service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable long id) {
        try {
            TaskDto task = service.findById(id);
            return ResponseEntity.ok(task);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@RequestBody Task task) {
        try {
            TaskDto saved = service.create(task);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Location", "/tasks/" + saved.getId())
                    .body(saved);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable long id, @RequestBody Task task) {
        task.setId(id);
        try {
            TaskDto updated = service.update(task);
            return ResponseEntity.ok(null);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTaskById(@PathVariable long id) {
        try {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> findAll(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam long userId) {
        if (from == null) from = LocalDateTime.MIN;
        if (to == null) to = LocalDateTime.MAX;
        List<TaskDto> tasks = service.findAll(from, to, userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> count(@RequestParam long userId) {
        return ResponseEntity.ok(service.countActiveTasksByUserId(userId));
    }
}