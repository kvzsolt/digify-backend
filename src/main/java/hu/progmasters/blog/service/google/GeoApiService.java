package hu.progmasters.blog.service.google;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeoApiService {

    @Value("${google.maps.api.key}")
    private String apiKey;
    private WebClient webClient;
    private String geocodingApiUrl = "https://maps.googleapis.com/maps/api/geocode/json";

    public GeoApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(geocodingApiUrl).build();
    }

    public String getGeocodingData(String location) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("address", location)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}