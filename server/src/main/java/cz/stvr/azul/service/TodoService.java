package cz.stvr.azul.service;

import cz.stvr.azul.dto.TodoForm;
import cz.stvr.azul.dto.TodoRequest;
import cz.stvr.azul.model.Todo;
import cz.stvr.azul.repository.TodoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

// v .NET by to bylo soucasti ApplicationDbContext
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

    public Todo create(TodoRequest request) {
        Todo todo = new Todo();
        mapIntoEntity(request, todo);
        return repo.save(todo);
    }

    public Todo update(Long id, TodoRequest request) {
        Todo todo = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Todo not found " + id));
        mapIntoEntity(request, todo);
        return todo;
    }


    public void toggleCompletion(Long id) {
        Todo todo = repo.getReferenceById(id);
        todo.setCompleted(!todo.getCompleted());
    }

    private void mapIntoEntity(TodoRequest request, Todo todo) {
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDueDate(request.getDueDate());
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Todo not found: " + id);
        }
        repo.deleteById(id);
    }

    private void apply(TodoRequest request, Todo todo) {
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDueDate(request.getDueDate());
    }

    public Optional<Todo> findById(Long id) {
        return repo.findById(id);
    }

    public Todo setCompletion(Long id, boolean completed) {
        Todo todo = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Todo not found: " + id));
        todo.setCompleted(completed);
        return todo;
    }
}