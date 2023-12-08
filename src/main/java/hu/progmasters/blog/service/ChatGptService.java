package hu.progmasters.blog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.progmasters.blog.dto.chatgpt.ChatGptGenerateReq;
import hu.progmasters.blog.dto.chatgpt.ChatGptRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@Transactional
public class ChatGptService {

    @Value("${openai.api.key}")
    private String apiKey;
    private final WebClient webClient;

    private final PostService postService;

    public ChatGptService(WebClient.Builder webClientBuilder, PostService postService) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.postService = postService;
    }

    public String responseContent(String message) {
        String JsoResponse = getChatResponse(message);
        String content;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(JsoResponse);

            content = jsonNode
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return content;
    }


    public String getModerateResponse() {
        String model = "gpt-3.5-turbo";

        String inputCommand = "{\"input\": \"Csütörtök délelőtt folytatódott a kirúgott karinthys tanárok munkaügyi pere a Fővárosi Törvényszéken. A tárgyalásról tudósító hvg.hu megírta, hogy a tegnapi tárgyaláson megjelent Rábel Krisztina Külső-Pesti Tankerületi Központ vezetője. A szabályos idézés ellenére korábban nem jelent meg a bíróságon, az alperes jogi képviselője szerint azért, mert nem kaptak idézést. Ezt a bíróság később cáfolta.\"}";

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(BodyInserters.fromValue(inputCommand))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getChatResponse(String message) {
        ChatGptRequest chatGptRequest = new ChatGptRequest(message);

        return webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(BodyInserters.fromValue(chatGptRequest))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String generatePicture(String prompt) {
        ChatGptGenerateReq chatGptGenerateReq = new ChatGptGenerateReq(prompt);

        return webClient.post()
                .uri("/images/generations")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(BodyInserters.fromValue(chatGptGenerateReq))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String generatePictureByTitle(Long id) {
        String prompt = postService.findPostById(id).getTitle();
        ChatGptGenerateReq chatGptGenerateReq = new ChatGptGenerateReq(prompt);

        return webClient.post()
                .uri("/images/generations")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(BodyInserters.fromValue(chatGptGenerateReq))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

