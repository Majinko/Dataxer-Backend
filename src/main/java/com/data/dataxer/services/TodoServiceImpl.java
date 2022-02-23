package com.data.dataxer.services;

import com.data.dataxer.models.domain.TodoList;
import com.data.dataxer.repositories.TodoListRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {
    @Autowired
    private TodoListRepository todoListRepository;

    @Override
    public List<TodoList> all() {
        return this.todoListRepository.findAllByAppProfileId(SecurityUtils.defaultProfileId());
    }
}
