package my.learn.mireaffjpractice5.service;


import my.learn.mireaffjpractice5.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    void createTask(Task t);
    List<Task> listTasks();
    List<Task> listDone(Boolean status);
    Optional<Task> getTaskById(Long id);
    void CreateMany(List<Task> tasks);

}
