const form = document.getElementById("chat-form");
const messageInput = document.getElementById("message");
const sendButton = document.getElementById("send-button");
const statusText = document.getElementById("status");
const errorText = document.getElementById("error");
const replySection = document.getElementById("reply-section");
const replyText = document.getElementById("reply");
let isSending = false;

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (isSending) return;

  errorText.hidden = true;
  errorText.textContent = "";
  const message = messageInput.value.trim();

  if (!message) {
    errorText.textContent = "Please enter a message.";
    errorText.hidden = false;
    messageInput.focus();
    return;
  }

  isSending = true;
  sendButton.disabled = true;
  sendButton.textContent = "Sending…";
  messageInput.disabled = true;
  replySection.hidden = true;
  replyText.textContent = "";
  statusText.textContent = "Ollama is thinking. The first reply may take a little longer.";
  form.setAttribute("aria-busy", "true");

  try {
    const response = await fetch("/chats", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ message })
    });

    // Handle non-JSON error pages as well as the API's JSON errors.
    const responseText = await response.text();
    let data;
    try {
      data = JSON.parse(responseText);
    } catch {
      throw new Error(response.ok
        ? "The server returned an unexpected response."
        : `Request failed (HTTP ${response.status}). Please try again.`);
    }

    if (!response.ok) {
      throw new Error(typeof data?.message === "string"
        ? data.message
        : `Request failed (HTTP ${response.status}). Please try again.`);
    }

    if (typeof data?.reply !== "string" || !data.reply.trim()) {
      throw new Error("Ollama returned no reply. Please try again.");
    }

    // Render model output as text, not HTML.
    replyText.textContent = data.reply;
    replySection.hidden = false;
    statusText.textContent = "Reply received.";
  } catch (error) {
    statusText.textContent = "";
    errorText.textContent = error instanceof TypeError
      ? "Cannot reach the chat API. Check that Spring Boot is running."
      : error.message || "Something went wrong. Please try again.";
    errorText.hidden = false;
  } finally {
    isSending = false;
    sendButton.disabled = false;
    sendButton.textContent = "Send message";
    messageInput.disabled = false;
    form.removeAttribute("aria-busy");
    messageInput.focus();
  }
});