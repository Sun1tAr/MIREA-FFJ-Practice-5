package my.learn.mireaffjpractice5.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt =  LocalDateTime.now();

    @Override
    public String toString() {
        return " | " + id +
                " | " + title + '\'' +
                " | done = " + done +
                " | " + createdAt +
                " | ";
    }
}
