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

    Optional<Task> findById(long id);
    @Query("SELECT t FROM Task t WHERE t.createdBy.id = :userId AND t.createdAt BETWEEN :from AND :to")
    List<Task> findAll(

            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("userId") Long userId
    );
    @Query("SELECT COUNT(t) FROM Task t WHERE t.createdBy.id = :userId AND t.status IN ('OPEN', 'IN_PROGRESS')")
    long countActiveTasksByUserId(@Param("userId") Long userId);

}