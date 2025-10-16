package cz.stvr.azul.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity // attribute [Table] v C#
@Table(name = "todos")
public class Todo {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment (pro H2 ?)
    private Long id;

    @Column(nullable = false) // NOT NULL
    private String title;

    @Column(length = 1000) // VARCHAR(1000)
    private String description;

    private boolean completed;
    private LocalDate dueDate;

    // getters and setters
    public Long getId() { return id; }

    public void setCompleted(boolean completed) { this.completed = completed; }
    public boolean getCompleted() { return completed; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getDueDate() { return dueDate; }
}
