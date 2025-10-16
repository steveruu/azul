package cz.stvr.azul.repository;

import cz.stvr.azul.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

// DbContext v .NET?
// Direct database access - tohle uz je ta nejnizsi vrstva k DB
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // extenduje JpaRepository

    // custom query - jako LINQ?
    // tady ten kod resi Spring sam !!! hodne cool
    Todo getReferenceById(Long id);

    // podobny method names follow conventions: findBy, existsBy, countBy, deleteBy
}

