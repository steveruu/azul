package cz.stvr.azul.service;

import cz.stvr.azul.dto.TodoForm;
import cz.stvr.azul.model.Todo;
import cz.stvr.azul.repository.TodoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class TodoService {
    private final TodoRepository repo;

    public TodoService(TodoRepository tr) {
        this.repo = tr;
    }

    public List<Todo> list() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "dueDate"));
    }

    public Todo create(TodoForm form) {
        Todo todo = new Todo();
        mapIntoEntity(form, todo);
        return repo.save(todo);
    }

    public Todo update(Long id, TodoForm form) {
        Todo todo = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Todo not found " + id));
        mapIntoEntity(form, todo);
        return todo;
    }

    public void toggleCompletion(Long id) {
        Todo todo = repo.getReferenceById(id);
        todo.setCompleted(!todo.getCompleted());
    }

    private void mapIntoEntity(TodoForm form, Todo todo) {
        todo.setTitle(form.getTitle());
        todo.setDescription(form.getDescription());
        todo.setDueDate(form.getDueDate());
    }
}