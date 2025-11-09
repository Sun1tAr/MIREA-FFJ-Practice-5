package my.learn.mireaffjpractice5.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.learn.mireaffjpractice5.model.Task;
import my.learn.mireaffjpractice5.repository.TaskRepository;
import my.learn.mireaffjpractice5.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public void createTask(Task t) {
        Task save = taskRepository.save(t);
        log.info("Inserted task id = {} ({})", save.getId(), save.getTitle());
    }

    @Override
    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> listDone(Boolean status) {
        return taskRepository.findTasksWithDoneStatus(status);
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public void CreateMany(List<Task> tasks) {
        log.info("Created {} tasks for 1 time", taskRepository.saveAll(tasks).size());
    }
}
