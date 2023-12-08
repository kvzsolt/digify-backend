package hu.progmasters.blog.dto.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatGptGenerateReq {
    @JsonProperty("model")
    private String model;

    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("n")
    private Integer n;

    @JsonProperty("size")
    private String size;

    public ChatGptGenerateReq(String prompt) {
        this.model = "dall-e-3";
        this.prompt = prompt;
        this.n = 1;
        this.size = "1024x1024";
    }
}
