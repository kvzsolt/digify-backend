package hu.progmasters.blog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.progmasters.blog.domain.Event;
import hu.progmasters.blog.dto.event.EventFormInfo;
import hu.progmasters.blog.repository.EventRepository;
import hu.progmasters.blog.service.google.GeoApiService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@Service
@Transactional
@AllArgsConstructor
public class EventService {

    private EventRepository eventRepository;
    private GeoApiService geoApiService;

    public void createEvent(@Valid EventFormInfo eventFormInfo) {
        Event hackatonEvent = new Event();
        String jsonResponse = geoApiService.getGeocodingData(eventFormInfo.getLocationName());

        hackatonEvent.setLatitude(transformLatitude(jsonResponse));
        hackatonEvent.setLongitude(transformLongitude(jsonResponse));
        hackatonEvent.setEventName(eventFormInfo.getEventName());

        eventRepository.save(hackatonEvent);

    }

    private double transformLatitude(String json) {
        double latitude;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            latitude = jsonNode
                    .path("results")
                    .path(0)
                    .path("geometry")
                    .path("location")
                    .path("lat")
                    .asDouble();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return latitude;
    }

    private double transformLongitude(String json) {
        double longitude;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            longitude = jsonNode
                    .path("results")
                    .path(0)
                    .path("geometry")
                    .path("location")
                    .path("lng")
                    .asDouble();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return longitude;
    }
}
