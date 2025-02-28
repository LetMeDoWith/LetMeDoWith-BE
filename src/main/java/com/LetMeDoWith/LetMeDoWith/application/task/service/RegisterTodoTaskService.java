package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterTodoTaskService {
    
    private final TodoTaskRepository todoTaskRepository;
    
    // todo: 단일 태스크 등록, 루틴 포함 태스크 등록 메서드 구현
}