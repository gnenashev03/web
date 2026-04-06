package ru.ssau.todo.exceptions;
public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(Long id) {
        super("Task with id=" + id + " not found"); 
    }
}