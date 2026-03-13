package ru.ssau.todo.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        result.put("username", authentication.getName());
        result.put("roles", authentication.getAuthorities());
        return result;
    }
}