package com.example.todo.service;

import com.example.todo.dto.TodoDto;
import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import com.example.todo.event.TodoProducer;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.exception.UserNotFoundException;
import com.example.todo.repository.TodoRepository;
import com.example.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;


    // 메서드 결과 캐시에 저장. 같은 요청 오면 캐시에서 반환. 캐시에 없는 경우 db
    // key 이름 =  메서드이름:user이름 하여 유저별 캐시 관리
    @Transactional(readOnly = true)
    @Cacheable(value = "todos",
            key = "#root.methodName + ':' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name")
    public List<TodoDto> getAllTodos() {
        log.info("DB에서 Todos 조회 중");

        User user = getCurrentUser();

        return todoRepository.findByUser(user)
                .stream()
                .map(todo -> TodoDto.changeEntityToDto(todo))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public TodoDto getTodoById(Long id) {
        User user = getCurrentUser();

        // optional로 반환. 값이 있으면 꺼내주고, 빈 값이면 exception 던지기
        Todo todo = todoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new TodoNotFoundException());

        return TodoDto.changeEntityToDto(todo);
    }



    // 데이터가 변경됐으니 해당 유저의 캐시 데이터 삭제
    @CacheEvict(value = "todos",
            key = "'getAllTodos:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name")
    public TodoDto createTodo(TodoDto dto) {
        User user = getCurrentUser();

        // 받은 dto로 새로운 entity 객체 생성 => 비영속 상태. jpa의 메서드를 거쳐야 영속상태
        Todo todo = Todo.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .completed(dto.isCompleted())
                .user(user)
                .build();
        
        // 실제 저장 후 kafka 메시지 전송
        TodoDto saved = TodoDto.changeEntityToDto(todoRepository.save(todo));

        // kafka 이벤트 전송
        todoProducer.sendTodoCreated(saved);
        
        return saved;
    }



    @CacheEvict(value = "todos",
            key = "'getAllTodos:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name")
    public TodoDto updateTodo(Long id,TodoDto dto) {
        User user = getCurrentUser();

        Todo todo = todoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new TodoNotFoundException());

        // 새로 받은 dto로 기존 엔티티 수정. dirty-checking
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setCompleted(dto.isCompleted());

        return TodoDto.changeEntityToDto(todo);
    }



    @CacheEvict(value = "todos",
            key = "'getAllTodos:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name")
    public void deleteTodo(Long id) {
        User user = getCurrentUser();

        Todo todo = todoRepository.findByIdAndUser(id, user)
                        .orElseThrow(() -> new TodoNotFoundException());

        todoRepository.delete(todo);
    }



    // 현재 로그인 한 유저의 entity를 찾아오는 메서드
    private User getCurrentUser() {
        String username = SecurityContextHolder // 로그인 정보 전역 저장소
                .getContext() // 현재 요청의 security context 객체 가져오기
                .getAuthentication() // 사용자 정보 담고 있는 authentication 객체 가져오기
                .getName(); // 사용자의 이름 꺼내기

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());

        return user;
    }
}
