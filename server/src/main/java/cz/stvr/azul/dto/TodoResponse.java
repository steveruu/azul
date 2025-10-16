package cz.stvr.azul.dto;

import cz.stvr.azul.model.Todo;

import java.time.LocalDate;

public class TodoResponse {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDate dueDate;

    public static TodoResponse fromEntity(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.id = todo.getId();
        response.title = todo.getTitle();
        response.description = todo.getDescription();
        response.completed = todo.getCompleted();
        response.dueDate = todo.getDueDate();
        return response;
    }

    // Getters required for JSON serialization
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
