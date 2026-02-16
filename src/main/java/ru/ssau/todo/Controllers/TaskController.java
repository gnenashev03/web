package ru.ssau.todo.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.service.TaskServiceInterface;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskServiceInterface service;
    public TaskController(TaskServiceInterface service) {
        this.service = service;
    }
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Task task) {
        try {
            Task savedTask = service.create(task);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Location", "/tasks/" + savedTask.getId())
                    .body(savedTask);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
        @PutMapping("/{id}")
        public ResponseEntity<?> update(@PathVariable long id, @RequestBody Task task) {
            task.setId(id);
            try {
                service.update(task);
                return ResponseEntity.ok().build();
            } catch (TaskNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            } catch (IllegalStateException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteTaskById(@PathVariable long id) {
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
    public ResponseEntity<java.util.List<Task>> findAll(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam long userId) {

        if (from == null) from = LocalDateTime.MIN;
        if (to == null) to = LocalDateTime.MAX;

        if (to.isBefore(from)) {
            LocalDateTime tmp = from;
            from = to;
            to = tmp;
        }

        return ResponseEntity.ok(service.findAll(from, to, userId));
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> count(@RequestParam long userId) {
        return ResponseEntity.ok(service.countActiveTasksByUserId(userId));
    }
}
