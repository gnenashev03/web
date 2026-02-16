package ru.ssau.todo.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.exceptions.TaskNotFoundException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.sql.Timestamp.*;

@Repository
@Profile("jdbc")
public class TaskJdbcRepository implements TaskRepository
{
    private final JdbcTemplate jdbcTemplate;
    public TaskJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setCreatedBy(rs.getLong("created_by"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        task.setCreatedAt( createdAt.toLocalDateTime());
        return task;
    };
    @Override
    public Task create(Task task) {
        String sql = "INSERT INTO task(title, status, created_by, created_at) " +
                "VALUES (?, ?, ?, ?) RETURNING id, created_at";
        return jdbcTemplate.queryForObject(sql, taskRowMapper,
                task.getTitle(),
                task.getStatus().name(),
                task.getCreatedBy(),
                Timestamp.valueOf(LocalDateTime.now()));
    }
    @Override
    public Optional<Task> findById(long id) {
        String sql="SELECT * FROM task WHERE id = ?";
        try {
            Task task = jdbcTemplate.queryForObject(sql,taskRowMapper, id);
            return Optional.of(task);
        }
        catch (org.springframework.dao.EmptyResultDataAccessException e)
        {
            return Optional.empty();
        }
    }

    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        String sql="SELECT * FROM task WHERE created_by = ? AND created_at >= ? AND created_at <= ? ";
        return this.jdbcTemplate.query(sql,taskRowMapper,
                userId, valueOf(from), valueOf(to)
        );

    }

    @Override
    public void update(Task task) throws TaskNotFoundException {
        String sql="UPDATE task SET title = ?, status = ? WHERE id = ? ";
        int count=this.jdbcTemplate.update(sql,taskRowMapper,
                task.getTitle(), task.getStatus().name(), task.getId());

        if(count ==0) {
            throw new TaskNotFoundException(task.getId());
        }
    }

    @Override
    public void deleteById(long id) {
        this.jdbcTemplate.update("DELETE FROM task WHERE id = ?",id);
    }

    @Override
    public long countActiveTasksByUserId(long userId) {
        return this.jdbcTemplate.queryForObject("SELECT COUNT(task.id) FROM task WHERE (created_by = ? " +
                "AND (status in ('OPEN', 'IN_PROGRESS')))", Long.class, userId);
    }
}

