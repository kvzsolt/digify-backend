package hu.progmasters.blog.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventFormInfo {

    private String eventName;
    private String locationName;
}
