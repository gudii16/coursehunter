package com.coursehunter.course.command.event;

import com.coursehunter.common.event.CourseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseEventPublisher {

    private static final String BINDING = "course-events-out-0";

    private final StreamBridge streamBridge;

    public void publish(CourseEvent event) {
        streamBridge.send(BINDING, event);
    }
}
