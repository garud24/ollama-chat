package com.himanshu.ollamachat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.himanshu.ollamachat.dto.OllamaRequest;
import com.himanshu.ollamachat.dto.OllamaResponse;
import com.himanshu.ollamachat.exception.OllamaUnavailableException;

@Service
public class ChatService {

    private final RestClient restClient = RestClient.create();
    @Value("${ollama.url}")
    private String ollamaUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    public String generateResponse(String message) {

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be blank or null");
        }

        return ollamaResponse(message);

    }

    public String ollamaResponse(String message) {
        //String url = "http://localhost:11434/api/generate";

        OllamaRequest request = new OllamaRequest(ollamaModel, message, false);

        try {
            OllamaResponse result = restClient.post()
                    .uri(ollamaUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(OllamaResponse.class);

            if (result == null || result.response() == null) {
                throw new IllegalStateException("Ollama returned no response");
            }

            return result.response();

        } catch (ResourceAccessException exception) {
            throw new OllamaUnavailableException(
                    "Cannot connect to Ollama. Please try again later.");
        }
    }

}
