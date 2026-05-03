package com.coursehunter.course.query.projector;

import com.coursehunter.common.event.CourseEvent;
import com.coursehunter.course.query.entity.CourseCatalogView;
import com.coursehunter.course.query.repository.CourseCatalogViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseCatalogProjector {

    private final CourseCatalogViewRepository repository;

    @Bean
    public Consumer<CourseEvent> courseEvents() {
        return event -> {
            log.info("Received CourseEvent type={} id={}", event.getEventType(), event.getId());
            switch (event.getEventType()) {
                case CREATED -> onCreate(event);
                case UPDATED -> onUpdate(event);
                case DELETED -> onDelete(event);
            }
        };
    }

    private void onCreate(CourseEvent event) {
        CourseCatalogView view = new CourseCatalogView();
        view.setId(event.getId());
        view.setTitle(event.getTitle());
        view.setDescription(event.getDescription());
        view.setInstructorName(event.getInstructorName());
        view.setStatus("DRAFT");
        view.setTotalSeats(event.getTotalSeats());
        view.setAvailableSeats(event.getTotalSeats());
        view.setStartDate(event.getStartDate());
        view.setEndDate(event.getEndDate());
        view.setTags(tagsArray(event));
        view.setUpdatedAt(LocalDateTime.now());
        repository.save(view);
    }

    private void onUpdate(CourseEvent event) {
        repository.findById(event.getId()).ifPresent(view -> {
            view.setTitle(event.getTitle());
            view.setDescription(event.getDescription());
            view.setInstructorName(event.getInstructorName());
            view.setStatus(event.getStatus());
            view.setTotalSeats(event.getTotalSeats());
            view.setTags(tagsArray(event));
            view.setStartDate(event.getStartDate());
            view.setEndDate(event.getEndDate());
            view.setUpdatedAt(LocalDateTime.now());
            repository.save(view);
        });
    }

    private void onDelete(CourseEvent event) {
        repository.deleteById(event.getId());
    }

    private String[] tagsArray(CourseEvent event) {
        return event.getTags() != null ? event.getTags().toArray(new String[0]) : new String[0];
    }
}
