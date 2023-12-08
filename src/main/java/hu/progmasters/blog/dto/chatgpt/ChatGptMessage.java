package hu.progmasters.blog.dto.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatGptMessage {

    @JsonProperty("role")
    private String role;

    @JsonProperty("content")
    private String content;

    public ChatGptMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
