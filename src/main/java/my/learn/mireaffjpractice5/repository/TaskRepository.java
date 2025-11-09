package my.learn.mireaffjpractice5.repository;

import my.learn.mireaffjpractice5.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select t from Task t where t.done = ?1")
    List<Task> findTasksWithDoneStatus(Boolean status);

}
