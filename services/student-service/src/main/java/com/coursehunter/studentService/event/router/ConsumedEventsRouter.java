package com.coursehunter.studentService.event.router;

import com.coursehunter.studentService.entity.EnrolStatus;
import com.coursehunter.studentService.event.handler.ConsumedEventsHandler;
import com.coursehunter.studentService.event.model.EnrollmentEvents;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConsumedEventsRouter {
    private final Map<EnrolStatus, ConsumedEventsHandler> handlersMap = new HashMap<>();

    // Spring will inject all beans of type ConsumedEventsHandler into the list as all
    // are annotated with @Component
    public ConsumedEventsRouter(List<ConsumedEventsHandler> handlerList) {
        for (ConsumedEventsHandler handler : handlerList) {
            handlersMap.put(handler.getEventType(), handler);
        }
    }

    public void routeEvent(EnrollmentEvents event) {
        ConsumedEventsHandler handler = handlersMap.get(event.getEventType());
        if (handler != null) {
            handler.handleEvent(event);
        } else {
            throw new RuntimeException("Invalid event type: " + event.getEventType());
        }

    }
}
