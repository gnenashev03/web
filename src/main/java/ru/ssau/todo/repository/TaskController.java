package ru.ssau.todo.repository;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.service.TaskRepository;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository repository;
    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable long id) {

        Optional<Task> task = repository.findById(id);

        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTaskById(@PathVariable long id)
    {
        Optional<Task> task = repository.findById(id);
        if(task.isPresent())
        {
            repository.deleteById(id);
            return  ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task )
    {
        Task savedTask=repository.create(task);
        //return ResponseEntity.created(URI.create("/tasks/"+savedTask.getId()) ).body(savedTask);
        return ResponseEntity.status(HttpStatus.CREATED).header("Location","/tasks/"+savedTask.getId()).body(savedTask);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable long id, @RequestBody Task task) throws TaskNotFoundException {
        Optional<Task> taskexist= repository.findById(id);
            if(taskexist.isEmpty())
            {
                return ResponseEntity.notFound().build();
            }
            else{
                Task taskupdated = taskexist.get();
                taskupdated.setTitle(task.getTitle());
                taskupdated.setStatus(task.getStatus());
                repository.update(taskupdated);
                return ResponseEntity.ok(taskupdated);
            }
    }
    @GetMapping
    public ResponseEntity<Task> findAll(@RequestParam(required = false)
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                        LocalDateTime from,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            LocalDateTime to,
                                                @RequestParam long userId) {
        if (from == null) {
            from = LocalDateTime.MIN;}
        if (to == null) {
                to = LocalDateTime.MAX;}
        List<Task> taska=repository.findAll(from, to, userId);
        return  ResponseEntity.ok().body((Task) taska);

    }
    @GetMapping("/active/count")
    public ResponseEntity<Long> count(@RequestParam long userId)
    {
        long count =repository.countActiveTasksByUserId(userId);
        return ResponseEntity.ok(count);
    }


}

