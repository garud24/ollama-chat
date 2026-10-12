# OllamaChat

A simple Spring Boot REST API that accepts a message and returns a response from a local language model through Ollama.

Built to practice controllers, DTOs, services, input validation, exception handling, and external HTTP calls.

## Features

- Generate replies using `qwen3:4b` through Ollama.
- Exchange JSON requests and responses.
- Reject null, empty, and whitespace-only messages.
- Return a clear error when the Ollama server cannot be reached.
- Configure the Ollama endpoint and model through application properties.

Each request is independent. Conversation history and persistence are planned for a future version.

## Tech stack

- Java 25
- Spring Boot 4.1.1 and Spring Web
- Maven
- Spring RestClient
- Ollama with `qwen3:4b`

## Setup

Install a JDK compatible with the project's Java 25 target and install [Ollama](https://ollama.com/).

Download the model:

```bash
ollama pull qwen3:4b
```

Ensure the Ollama server is running at `http://localhost:11434`. If it is not already running through the Ollama application, start it in a separate terminal:

```bash
ollama serve
```

Configure `src/main/resources/application.properties`:

```properties
spring.application.name=ollamachat
ollama.url=http://localhost:11434/api/generate
ollama.model=qwen3:4b
```

From the project root, start Spring Boot:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080` by default.

## Send a message

```bash
curl -i -X POST http://localhost:8080/chats \
  -H "Content-Type: application/json" \
  -d '{"message":"Explain dependency injection in one sentence."}'
```

Example response (`200 OK`; wording varies):

```json
{
  "reply": "Dependency injection supplies an object's dependencies from outside rather than having the object create them itself."
}
```

The API waits for a complete model response (`stream: false`).

## Error responses

| Situation | Status |
| --- | --- |
| Valid message and successful model response | `200 OK` |
| Null, missing, empty, or whitespace-only message | `400 Bad Request` |
| Connection or network access failure calling Ollama | `503 Service Unavailable` |

Invalid input:

```json
{"message":"Message cannot be blank or null"}
```

Ollama connection failure:

```json
{"message":"Cannot connect to Ollama. Please try again later."}
```

Handling HTTP errors returned by Ollama, such as an unavailable model, is a future improvement.

## Request flow

1. `ChatController` receives the message in a `ChatRequest`.
2. `ChatService` validates the message.
3. `RestClient` sends an `OllamaRequest` to Ollama's `/api/generate` endpoint.
4. The service reads the reply from `OllamaResponse`.
5. The controller returns a `ChatResponse` as JSON.

`GlobalExceptionHandler` converts invalid-input and Ollama connection exceptions into API error responses.

## Planned improvements

- Store conversation history.
- Support separate conversations.
- Add database persistence.
- Handle additional Ollama error responses and configure request timeouts.
- Add a simple chat interface.
