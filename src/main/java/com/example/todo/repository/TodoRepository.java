package com.example.todo.repository;

import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo,Long> {


    // fetch join 처리 -> entitygraph 애노테이션
    // 한번에 같이 가져올 엔티티의 필드명 명시 -> attributepath
    @EntityGraph(attributePaths = "user")
    List<Todo> findByUser(User user);

    @EntityGraph(attributePaths = "user")
    Optional<Todo> findByIdAndUser(Long id, User user);

}
