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
}
