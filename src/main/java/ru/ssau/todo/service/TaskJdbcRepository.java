package ru.ssau.todo.service;

import org.springframework.context.annotation.Profile;
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
    @Override
    public Task create(Task task) {
        String sql = "INSERT INTO task(title, status, created_by, created_at) " +
                "VALUES (?, ?, ?, ?) RETURNING id, created_at";
        return this.jdbcTemplate.queryForObject(sql,
                (resset,rowNum) ->{
                    Task t =new Task();
                    t.setId(resset.getLong("id"));
                    t.setTitle(task.getTitle());
                    t.setStatus(task.getStatus());
                    t.setCreatedBy(task.getCreatedBy());
                    t.setCreatedAt(resset.getTimestamp("created_at").toLocalDateTime());
                    return t;
                },
                task.getTitle(), task.getStatus().name(), task.getCreatedBy(), Timestamp.valueOf(LocalDateTime.now()));
    }
    @Override
    public Optional<Task> findById(long id) {
        String sql="SELECT * FROM task WHERE id = ?";
        try {
            Task task = jdbcTemplate.queryForObject(sql,
                    (resset, rowNum) -> {
                        Task t = new Task();
                        t.setId(resset.getLong("id"));
                        t.setTitle(resset.getString("title"));
                        t.setStatus(TaskStatus.valueOf(resset.getString("status")));
                        t.setCreatedBy(resset.getLong("created_by"));
                        t.setCreatedAt(resset.getTimestamp("created_at").toLocalDateTime());
                        return t;
                    },
                    id);
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
       return this.jdbcTemplate.query(sql,
               (resset,rowNum) -> {
                   Task t = new Task();
                   t.setId(resset.getLong("id"));
                   t.setTitle(resset.getString("title"));
                   t.setStatus(TaskStatus.valueOf(resset.getString("status")));
                   t.setCreatedBy(resset.getLong("created_by"));
                   t.setCreatedAt(resset.getTimestamp("created_at").toLocalDateTime());
                   return t;
               },
               userId, valueOf(from), valueOf(to)
        );

    }

    @Override
    public void update(Task task) throws TaskNotFoundException {
        String sql="UPDATE task SET title = ?, status = ? WHERE id = ? ";
        int count=this.jdbcTemplate.update(sql,
                    task.getTitle(), task.getStatus().name(), task.getId());

        if(count ==0) {
            throw new TaskNotFoundException(task.getId());
            }
    }

    @Override
    public void deleteById(long id) {
        this.jdbcTemplate.update("delete from task where id = ?",id);
    }

    @Override
    public long countActiveTasksByUserId(long userId) {
        return this.jdbcTemplate.queryForObject("select count(task.id) from task where (created_by = ? " +
                "and (status in ('OPEN', 'IN_PROGRESS')))", Long.class, userId);
    }
}

