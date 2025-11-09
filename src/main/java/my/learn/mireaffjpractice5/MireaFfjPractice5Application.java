package my.learn.mireaffjpractice5;

import lombok.extern.slf4j.Slf4j;
import my.learn.mireaffjpractice5.model.Task;
import my.learn.mireaffjpractice5.service.TaskService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Slf4j
public class MireaFfjPractice5Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(MireaFfjPractice5Application.class, args);

        TaskService bean = run.getBean(TaskService.class);

        List<Task> starts = new ArrayList<>();
        for  (int i = 1; i < 16; i++) {
            starts.add(Task.builder()
                            .title("Сделать ПЗ №" + i)
                            .done(i < 6)
                            .createdAt(LocalDateTime.now())
                    .build());
        }

        log.info("== Начальный список задач ==");
        bean.listTasks().forEach(
                t -> log.info(t.toString())
        );

        log.info("== Вставка задач ==");
        for (int i = 0; i < starts.size()/2; i++) {
            bean.createTask(starts.get(i));
        }

        log.info("== Первая вставка - список задач ==");
        bean.listTasks().forEach(
                t -> log.info(t.toString())
        );

        log.info("== Выполненные задачи ==");
        bean.listDone(true).forEach(
                t -> log.info(t.toString())
        );

        log.info("== Задача с id = 6 ==");
        log.info(bean.getTaskById(6L).toString());

        log.info("== Множественная вставка ==");
        List <Task> insertable = new ArrayList<>();
        for (int i = starts.size()/2; i < starts.size(); i++) {
            insertable.add(starts.get(i));
        }
        bean.CreateMany(insertable);

        log.info("== Конечный список задач ==");
        bean.listTasks().forEach(
                t -> log.info(t.toString())
        );
    }

}
