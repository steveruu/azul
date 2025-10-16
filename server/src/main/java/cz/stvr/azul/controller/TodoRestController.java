package cz.stvr.azul.controller;

import cz.stvr.azul.dto.TodoRequest;
import cz.stvr.azul.dto.TodoResponse;
import cz.stvr.azul.model.Todo;
import cz.stvr.azul.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

// REST API controller
@RestController
@RequestMapping("/api/todos")
public class TodoRestController {
    private final TodoService service;

    public TodoRestController(TodoService ts) {
        this.service = ts;
    }

    // CRUD operations

    // GET /api/todos
    @GetMapping
    public List<TodoResponse> list() {
        return service.list().stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // GET /api/todos/{id}
    @GetMapping("/{id}")
    public TodoResponse getById(@PathVariable Long id) {
        return service.findById(id)
                .map(TodoResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // Create - POST /api/todos
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse create(@Valid @RequestBody TodoRequest request) {
        Todo todo = service.create(request);
        return TodoResponse.fromEntity(todo);
    }

    // Update - PUT /api/todos/{id}
    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable Long id,
                               @Valid @RequestBody TodoRequest request) {
        Todo todo = service.update(id, request);
        return TodoResponse.fromEntity(todo);
    }

    // PATCH /api/todos/{id}/complete?completed=true
    @PatchMapping("/{id}/complete")
    public TodoResponse toggleCompletion(@PathVariable Long id, @RequestParam boolean completed) {
        Todo todo = service.setCompletion(id, completed);
        return TodoResponse.fromEntity(todo);
    }

    // DELETE /api/todos/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
