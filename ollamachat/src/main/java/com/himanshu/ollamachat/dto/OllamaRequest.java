package com.himanshu.ollamachat.dto;

public record OllamaRequest(
    String model,
    String prompt,
    boolean Stream)
    {
}
