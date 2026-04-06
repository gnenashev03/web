package ru.ssau.todo.entity.dto;

import ru.ssau.todo.entity.TaskStatus;
import java.time.LocalDateTime;

public class TaskDto {
    private Long id;
    private String title;
    private TaskStatus status;
    private Long createdById;
    private LocalDateTime createdAt;

    public TaskDto() {}

    public TaskDto(Long id, String title, TaskStatus status, Long createdById, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.createdById = createdById;
        this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public Long getCreatedBy() { return createdById; }
    public void setCreatedBy(Long createdById) { this.createdById = createdById; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}