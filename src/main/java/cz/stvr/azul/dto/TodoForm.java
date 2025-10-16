package cz.stvr.azul.dto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class TodoForm {
    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 1000, message = "Description too long")
    private String description;

    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;
}
