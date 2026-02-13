package ru.ssau.todo.repository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.service.TaskRepository;

import java.time.LocalDateTime;
import java.util.*;
    @Repository
    @Profile("inMemory")
    public class TaskInMemoryRepository implements TaskRepository {
        private Map<Long, Task> taskslist = new HashMap<>();
        private long idcounter = 0 ;
        @Override
        public Task create(Task task)
        {
            long newId = 0;
            if (task == null) throw new IllegalArgumentException("not null");
            newId=++idcounter;

            Task newTask= new Task();
            newTask.setId(newId);
            newTask.setTitle(task.getTitle());
            newTask.setStatus(task.getStatus());
            newTask.setCreatedBy(task.getCreatedBy());
            newTask.setCreatedAt(LocalDateTime.now());

            taskslist.put(newId, newTask);
            return newTask;
        }
        @Override
        public Optional<Task> findById(long id)
        {
            return Optional.ofNullable(taskslist.get(id));
        }
        @Override
        public List <Task> findAll(LocalDateTime from,LocalDateTime to, long userId)
        {
            List <Task> results= new ArrayList<>();
            for( Task task: taskslist.values())
            {
                if(task.getCreatedBy()==userId && (!task.getCreatedAt().isBefore(from) && !task.getCreatedAt().isAfter(to) ))
                {
                    results.add(task);
                }
            }
            return results;
        }
        @Override
        public void update(Task task) throws TaskNotFoundException
        {
            if( task == null ) throw new IllegalArgumentException("not null");
            Task taskcurrent=taskslist.get(task.getId());
            if( taskcurrent == null) {
                throw new TaskNotFoundException(task.getId());
            }
            taskcurrent.setTitle((task.getTitle()));
            taskcurrent.setStatus(task.getStatus());
        }
        @Override
        public void deleteById( long id)
        {
            taskslist.remove( id);
        }
        @Override
        public long countActiveTasksByUserId(long userId)
        {
            long count =0;
            for(Task task: taskslist.values())
            {
                if(task.getCreatedBy()== userId && (task.getStatus()==TaskStatus.OPEN || task.getStatus()==TaskStatus.IN_PROGRESS))
                {
                    count++;
                }
            }
            return count;
        }


}
