# Практическое занятие №5: Подключение к PostgreSQL через database/sql и Spring Data JPA

## Выполнил: Туев Д. ЭФМО-01-25

## Описание проекта

Проект реализует приложение для управления задачами (Task Management System) с 
использованием Spring Boot и PostgreSQL. Приложение демонстрирует работу с 
реляционной базой данных через Spring Data JPA, включая выполнение основных операций CRUD 
(Create, Read, Update, Delete) над сущностью Task. Система позволяет создавать задачи, сохранять их в базе данных, 
извлекать по различным критериям (все задачи, выполненные задачи, задача по ID) и работать с множественными вставками 
данных.

### Стек технологий

В проекте используются следующие технологии и инструменты:

- **Spring Boot 3.5.7** — основной фреймворк приложения
- **Spring Data JPA** — облегчённая работа с реляционной БД через ORM
- **PostgreSQL** — система управления реляционной базой данных
- **Lombok** — сокращение шаблонного кода (геттеры, сеттеры, конструкторы, логирование)
- **Maven** — управление зависимостями и сборка проекта
- **Java 17** — язык программирования
- **Jakarta Persistence API** — стандартная Java API для работы с сущностями


### Поддерживаемые операции

| Операция | Метод | Описание |
| :-- | :-- | :-- |
| **CREATE** (создание) | `createTask(Task)` | Создаёт одну новую задачу в БД |
| **READ** (чтение) | `listTasks()` | Получает все задачи из БД |
| **READ** (фильтрация) | `listDone(Boolean)` | Получает задачи по статусу выполнения |
| **READ** (по ID) | `getTaskById(Long)` | Получает конкретную задачу по ID |
| **CREATE** (множественные) | `CreateMany(List<Task>)` | Создаёт несколько задач за один раз |

### Структура проекта

```
MIREA-FFJ-Practice-5/
├── src/main/java/my/learn/mireaffjpractice5/
│   ├── MireaFfjPractice5Application.java      # Точка входа приложения
│   ├── model/
│   │   └── Task.java                          # Доменная модель Task (Entity)
│   ├── repository/
│   │   └── TaskRepository.java                # JPA репозиторий для работы с БД
│   └── service/
│       ├── TaskService.java                   # Интерфейс сервиса
│       └── impl/
│           └── TaskServiceImpl.java            # Реализация сервиса с бизнес-логикой
├── pom.xml                                    # Конфигурация Maven и зависимости
└── application.properties                     # Конфигурация приложения (БД, логирование)
```

## Результат выполнения

![img.png](docs/img.png)

## Теоретические основы

### Spring Data JPA и ORM

Spring Data JPA предоставляет высокоуровневую абстракцию над 
Hibernate ORM (Object-Relational Mapping). 
Вместо написания SQL запросов вручную, разработчик работает с объектами Java, а JPA автоматически преобразует их в SQL операции.

**Основные компоненты:**

- **Entity** — класс, аннотированный `@Entity`, представляет таблицу в БД
- **Repository** — интерфейс для выполнения CRUD операций, наследует `JpaRepository`
- **Service** — бизнес-логика приложения, использует репозиторий для работы с данными
- **Transactional** — аннотация для управления транзакциями


### Жизненный цикл сущности

1. **New** — новый объект, не привязан к сессии JPA
2. **Managed** — объект привязан к сессии, отслеживаются изменения
3. **Detached** — объект был в сессии, но сессия закрыта
4. **Removed** — объект отмечен для удаления

### Query методы в Repository

Spring Data JPA позволяет определять запросы несколькими способами:

```java
// 1. Через именование метода (query by method name)
List<Task> findByDone(Boolean done);

// 2. Через аннотацию @Query с JPQL
@Query("select t from Task t where t.done = ?1")
List<Task> findTasksWithDoneStatus(Boolean status);

// 3. Через Native Query
@Query(value = "SELECT * FROM tasks WHERE done = ?1", nativeQuery = true)
List<Task> findByDoneNative(Boolean done);
```


## Ключевые фрагменты кода

### Определение сущности Task

```java
@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Boolean done = false;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

Аннотация `@Entity` помечает класс как сущность JPA. `@Table` указывает имя таблицы в БД. `@Id` обозначает первичный ключ, `@GeneratedValue` — автоматическое увеличение ID. `@Column` задаёт параметры колонки (nullable, unique и т.д.).

### JPA Repository

```java
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select t from Task t where t.done = ?1")
    List<Task> findTasksWithDoneStatus(Boolean status);
}
```

Наследуя `JpaRepository<Task, Long>`, репозиторий получает готовые методы `save()`, `findAll()`, `findById()`, `delete()`. Параметры `<Task, Long>` указывают тип сущности и тип первичного ключа.

### Сервис с бизнес-логикой

```java
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
    public List<Task> listDone(Boolean status) {
        return taskRepository.findTasksWithDoneStatus(status);
    }
}
```

`@Service` помечает класс как сервис. `@RequiredArgsConstructor` из Lombok генерирует конструктор с обязательными полями. `@Transactional` обеспечивает управление транзакциями: если метод завершится исключением, изменения откатываются.

### Использование в Application классе

```java
@SpringBootApplication
@Slf4j
public class MireaFfjPractice5Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(MireaFfjPractice5Application.class, args);
        TaskService bean = run.getBean(TaskService.class);

        List<Task> starts = new ArrayList<>();
        for (int i = 1; i < 16; i++) {
            starts.add(Task.builder()
                    .title("Сделать ПЗ №" + i)
                    .done(i < 6)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        log.info("== Вставка задач ==");
        for (int i = 0; i < starts.size()/2; i++) {
            bean.createTask(starts.get(i));
        }

        log.info("== Множественная вставка ==");
        List<Task> insertable = new ArrayList<>();
        for (int i = starts.size()/2; i < starts.size(); i++) {
            insertable.add(starts.get(i));
        }
        bean.CreateMany(insertable);
    }
}
```

Приложение инициализирует Spring контекст, извлекает сервис из контекста и выполняет операции с БД.

## Контрольные вопросы

### 1. Чем отличается JPA от JDBC?

**JDBC (Java Database Connectivity)** — низкоуровневый API для прямого подключения к БД. 
Разработчик пишет SQL запросы вручную, затем парсит результаты.

**JPA (Java Persistence API)** — высокоуровневая спецификация для ORM. 
Работает с объектами, автоматически преобразуя их в SQL запросы. JPA 
скрывает детали работы с БД, позволяя менять реализацию (Hibernate, EclipseLink) без изменения кода.


| Аспект | JDBC | JPA |
| :-- | :-- | :-- |
| Уровень абстракции | Низкий | Высокий |
| Написание запросов | SQL вручную | JPQL/методы |
| Маппинг результатов | Ручное | Автоматическое |
| Производительность | Выше (меньше слоёв) | Ниже (больше абстракции) |
| Гибкость | Высокая | Средняя |

### 2. Что такое EntityManager и как его использовать?

**EntityManager** — главный объект JPA, управляющий жизненным циклом сущностей. Выполняет 
операции persist (вставка), merge (обновление), remove (удаление), find (поиск).

```java
// Получение EntityManager
@PersistenceContext
private EntityManager entityManager;

// Вставка новой сущности
Task task = new Task();
task.setTitle("Моя задача");
entityManager.persist(task);  // task теперь в managed состоянии

// Поиск по ID
Task found = entityManager.find(Task.class, 1L);

// Обновление (для managed объекта изменения сохраняются автоматически)
found.setTitle("Обновлённое имя");  // Изменения отследят на commit/flush

// Удаление
entityManager.remove(found);
```

В Spring Data JPA репозитории скрывают работу с EntityManager, но он доступен для сложных операций.

### 3. Какие типы связей поддерживает JPA?

JPA поддерживает четыре типа связей между сущностями:

**@OneToOne** — один-к-одному. Например, сотрудник и его профиль.

```java
@OneToOne
@JoinColumn(name = "profile_id")
private Profile profile;
```

**@OneToMany** — один-ко-многим. Например, проект имеет много задач.

```java
@OneToMany(mappedBy = "project")
private List<Task> tasks;
```

**@ManyToOne** — много-к-одному. Обратная сторона @OneToMany.

```java
@ManyToOne
@JoinColumn(name = "project_id")
private Project project;
```

**@ManyToMany** — много-ко-многим. Например, студент и курсы.

```java
@ManyToMany
@JoinTable(name = "student_courses",
           joinColumns = @JoinColumn(name = "student_id"),
           inverseJoinColumns = @JoinColumn(name = "course_id"))
private List<Course> courses;
```


### 4. Как работает кэширование первого уровня в JPA?

Кэш первого уровня (Session Cache) встроен в EntityManager. Все загруженные и управляемые объекты хранятся в кэше в рамках одной сессии.

```java
Task task1 = entityManager.find(Task.class, 1L);  // Запрос в БД
Task task2 = entityManager.find(Task.class, 1L);  // Из кэша, запроса нет
assert task1 == task2;  // Один объект в памяти
```

После закрытия сессии (закрытия транзакции) кэш очищается.

### 5. Что такое N+1 проблема и как её избежать?

**N+1 проблема** — когда для загрузки коллекции выполняется 1 запрос, а затем N запросов для каждого элемента коллекции.

```java
// Плохо: N+1 запросы
List<Project> projects = projectRepository.findAll();  // 1 запрос
for (Project p : projects) {
    p.getTasks().forEach(...);  // N запросов для загрузки задач каждого проекта
}

// Хорошо: с fetch join (1 запрос)
@Query("select p from Project p join fetch p.tasks")
List<Project> findAllWithTasks();

// Или lazy loading отключить и использовать @EntityGraph
@EntityGraph(attributePaths = "tasks")
List<Project> findAll();
```


### 6. В чём разница между @Transactional на методе и на классе?

**На методе** — транзакция управляется для конкретного метода.

```java
@Service
public class TaskServiceImpl implements TaskService {
    @Transactional
    public void createTask(Task t) {
        // Транзакция начинается и заканчивается здесь
    }
}
```

**На классе** — все public методы класса выполняются в транзакции.

```java
@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    public void createTask(Task t) { ... }  // Транзакция
    public void deleteTask(Long id) { ... }  // Транзакция
}
```

Обычно используется на классе для удобства, но можно переопределить на методе для специальных случаев.

## Выводы

Данный проект демонстрирует интеграцию Spring Boot с PostgreSQL через Spring Data JPA. Основные достижения:

1. **Успешное подключение** к PostgreSQL и выполнение CRUD операций через Spring Data JPA
2. **Правильное разделение** на слои: Entity → Repository → Service → Application
3. **Использование аннотаций** для маппинга сущностей и управления транзакциями
4. **Применение JPQL** для создания кастомных запросов через @Query
5. **Множественные операции** с использованием `saveAll()` для оптимизации производительности

Spring Data JPA значительно упрощает работу с БД по сравнению с raw JDBC, обеспечивая безопасность, читаемость и 
поддерживаемость кода. Для боевых систем рекомендуется добавить обработку ошибок, валидацию, логирование и кэширование.