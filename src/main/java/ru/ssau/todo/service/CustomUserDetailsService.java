package ru.ssau.todo.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Role;
import ru.ssau.todo.entity.User;
import ru.ssau.todo.entity.dto.UserDto;
import ru.ssau.todo.repository.RoleRepository;
import ru.ssau.todo.repository.UserRepository;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }

    public void register(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Role role;
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            role = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() ->
                    new RuntimeException("ROLE_ADMIN not found"));
        } else {
            role = roleRepository.findByName("ROLE_USER").orElseThrow(() ->
                    new RuntimeException("ROLE_USER not found"));
        }
        user.getRoles().add(role);
        userRepository.save(user);
    }
}