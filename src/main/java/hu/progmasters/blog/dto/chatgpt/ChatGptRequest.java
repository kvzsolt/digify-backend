package hu.progmasters.blog.dto.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data

public class ChatGptRequest {
    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<ChatGptMessage> messages;

    public ChatGptRequest(String message) {
        this.model = "gpt-3.5-turbo-1106";
        this.messages =
                List.of(
//                new ChatGptMessage("system", "You are a helpful assistant."),
                new ChatGptMessage("user", message)
        );
    }
}
