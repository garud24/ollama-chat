package com.himanshu.ollamachat.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.himanshu.ollamachat.dto.ChatRequest;
import com.himanshu.ollamachat.dto.ChatResponse;
import com.himanshu.ollamachat.service.ChatService;


@RestController 
public class ChatController {

    private final ChatService service;

    public ChatController(ChatService service)
    {
        this.service = service;
    }

    @PostMapping("/chats")
    public ChatResponse createChat(@RequestBody ChatRequest request) {        
        return new ChatResponse(service.generateResponse(request.message()));
    }
    
}
