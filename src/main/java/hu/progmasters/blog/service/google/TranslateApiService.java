package hu.progmasters.blog.service.google;

import hu.progmasters.blog.dto.google.TranslateReq;
import hu.progmasters.blog.dto.google.TranslateRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service
public class TranslateApiService {

    private final GoogleAuthService googleAuthService;
    private static final String API_URL = "https://translation.googleapis.com/language/translate/v2";

    public TranslateApiService(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    public TranslateRes translate(TranslateReq translateRequest) throws IOException {
        WebClient webClient = WebClient.create();

        String accessToken = googleAuthService.authGoogle().getAccessToken().getTokenValue();

        return webClient.post()
                .uri(API_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(BodyInserters.fromValue(translateRequest))
                .retrieve()
                .bodyToMono(TranslateRes.class)
                .block();
    }
}
