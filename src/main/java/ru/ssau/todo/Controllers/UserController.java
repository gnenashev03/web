package ru.ssau.todo.Controllers;

import ru.ssau.todo.entity.dto.UserDto;
import ru.ssau.todo.service.CustomUserDetailsService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CustomUserDetailsService userService;

    public UserController(CustomUserDetailsService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void register(@RequestBody UserDto dto) {
        userService.register(dto);
    }
}