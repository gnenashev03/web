package ru.ssau.todo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.exceptions.TaskNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    //Optional<Task> findById(long id);
    @Query(nativeQuery = true,
            value = "SELECT * FROM Task WHERE created_at BETWEEN :from AND :to AND created_by = :userId")
    List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId);
    @Query(value ="SELECT COUNT(t) FROM Task t WHERE t.createdBy.id = :userId AND t.status IN ('OPEN', 'IN_PROGRESS')")
    long countActiveTasksByUserId(@Param("userId") Long userId);

}