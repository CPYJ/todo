package com.example.todo.service;

import com.example.todo.dto.TodoDto;
import com.example.todo.entity.Todo;
import com.example.todo.event.TodoProducer;
import com.example.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoProducer todoProducer;

    // 메서드 결과 캐시에 저장. 같은 요청 오면 캐시에서 반환. 캐시에 없는 경우 db
    @Cacheable(value = "todos")
    public List<TodoDto> getAllTodos() {
        log.info("DB에서 Todos 조회 중");
        return todoRepository.findAll()
                .stream()
                .map(todo -> TodoDto.changeEntityToDto(todo))
                .collect(Collectors.toList());
    }


    public TodoDto getTodoById(Long id) {
        // optional로 반환. 값이 있으면 꺼내주고, 빈 값이면 exception 던지기
        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Can't find a todo id " + id)
        );
        return TodoDto.changeEntityToDto(todo);
    }


    // 데이터가 변경됐으니 todos 캐시 내 모든 항목 지우기
    @CacheEvict(value= "todos", allEntries = true)
    public TodoDto createTodo(TodoDto dto) {
        // 받은 dto로 새로운 entity 객체 생성 => 비영속 상태. jpa의 메서드를 거쳐야 영속상태
        Todo todo = Todo.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .completed(dto.isCompleted())
                .build();
        
        // 실제 저장 후 kafka 메시지 전송
        TodoDto saved = TodoDto.changeEntityToDto(todoRepository.save(todo));

        // kafka 이벤트 전송
        todoProducer.sendTodoCreated(saved);
        
        return saved;
    }



    @CacheEvict(value= "todos", allEntries = true)
    public TodoDto updateTodo(Long id,TodoDto dto) {
        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Can't find a todo id " + id)
        );

        // 새로 받은 dto로 기존 엔티티 수정
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setCompleted(dto.isCompleted());

        return TodoDto.changeEntityToDto(todo);
    }


    @CacheEvict(value= "todos", allEntries = true)
    public void deleteTodo(Long id) {
        if(!todoRepository.existsById(id)) {
            throw new RuntimeException("Can't find a todo id " + id);
        }
        todoRepository.deleteById(id);
    }
}
