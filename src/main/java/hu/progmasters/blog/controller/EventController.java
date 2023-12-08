package hu.progmasters.blog.controller;

import hu.progmasters.blog.dto.event.EventFormInfo;
import hu.progmasters.blog.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
@Slf4j
public class EventController {

    private EventService eventService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity createEvent(@Valid @RequestBody EventFormInfo eventFormInfo) {
        eventService.createEvent(eventFormInfo);
        return new ResponseEntity(HttpStatus.OK);
    }
}
