package ru.ssau.todo.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/success")
    public String success() {
        return "{\"status\":\"ok\",\"message\":\"Login successful\"}";
    }

    @GetMapping("/error")
    public String error() {
        return "{\"status\":\"fail\",\"message\":\"Login failed\"}";
    }
}