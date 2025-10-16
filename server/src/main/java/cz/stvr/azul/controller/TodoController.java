package cz.stvr.azul.controller;

import cz.stvr.azul.dto.TodoForm;
import cz.stvr.azul.service.TodoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

// unused
@Controller
@RequestMapping("/todos")
public class TodoController {
    private final TodoService service;

    public TodoController(TodoService ts) {
        this.service = ts; // ts pmo
    }

    @ModelAttribute("todoForm")
    public TodoForm todoForm() {
        return new TodoForm();
    }
}
