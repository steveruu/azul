package cz.stvr.azul.repository;

import cz.stvr.azul.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    Todo getReferenceById(Long id);
}

