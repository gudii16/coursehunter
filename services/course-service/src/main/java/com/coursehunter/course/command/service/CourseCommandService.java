package com.coursehunter.course.command.service;

import com.coursehunter.common.event.CourseEvent;
import com.coursehunter.common.event.CourseEventType;
import com.coursehunter.course.command.dto.CourseResponse;
import com.coursehunter.course.command.dto.CreateCourseRequest;
import com.coursehunter.course.command.dto.UpdateCourseRequest;
import com.coursehunter.course.command.entity.Course;
import com.coursehunter.course.command.entity.CourseStatus;
import com.coursehunter.course.command.entity.Tag;
import com.coursehunter.course.command.event.CourseEventPublisher;
import com.coursehunter.course.command.repository.CourseRepository;
import com.coursehunter.course.command.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseCommandService {

    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;
    private final CourseEventPublisher eventPublisher;

    @Transactional
    public CourseResponse create(CreateCourseRequest request) {
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setInstructorName(request.getInstructorName());
        course.setTotalSeats(request.getTotalSeats());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());

        if (request.getTagSlugs() != null) {
            course.setTags(resolveTags(request.getTagSlugs()));
        }

        Course saved = courseRepository.save(course);
        eventPublisher.publish(toEvent(saved, CourseEventType.CREATED));
        return toResponse(saved);
    }

    @Transactional
    public CourseResponse update(UUID id, UpdateCourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));

        if (request.getTitle() != null)          course.setTitle(request.getTitle());
        if (request.getDescription() != null)    course.setDescription(request.getDescription());
        if (request.getInstructorName() != null) course.setInstructorName(request.getInstructorName());
        if (request.getTotalSeats() != null)     course.setTotalSeats(request.getTotalSeats());
        if (request.getStartDate() != null)      course.setStartDate(request.getStartDate());
        if (request.getEndDate() != null)        course.setEndDate(request.getEndDate());
        if (request.getTagSlugs() != null)       course.setTags(resolveTags(request.getTagSlugs()));

        Course saved = courseRepository.save(course);
        eventPublisher.publish(toEvent(saved, CourseEventType.UPDATED));
        return toResponse(saved);
    }

    @Transactional
    public CourseResponse publish(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
        course.setStatus(CourseStatus.PUBLISHED);
        Course saved = courseRepository.save(course);
        eventPublisher.publish(toEvent(saved, CourseEventType.UPDATED));
        return toResponse(saved);
    }

    @Transactional
    public CourseResponse archive(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
        course.setStatus(CourseStatus.ARCHIVED);
        Course saved = courseRepository.save(course);
        eventPublisher.publish(toEvent(saved, CourseEventType.UPDATED));
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        courseRepository.deleteById(id);
        eventPublisher.publish(CourseEvent.builder()
                .eventType(CourseEventType.DELETED)
                .id(id)
                .build());
    }

    // ── Helpers ──────────────────────────────────────────────

    private CourseEvent toEvent(Course course, CourseEventType type) {
        return CourseEvent.builder()
                .eventType(type)
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorName(course.getInstructorName())
                .status(course.getStatus().name())
                .totalSeats(course.getTotalSeats())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .tags(slugsOf(course))
                .build();
    }

    private Set<Tag> resolveTags(Set<String> tags) {
        return tags.stream()
                .map(tag -> tagRepository.findBySlug(String.join("-",tag.split(" ")).toLowerCase())
                        .orElseGet(() -> tagRepository.save(new Tag(tag, String.join("-",tag.split(" ")).toLowerCase()))))
                .collect(Collectors.toSet());
    }

    private Set<String> slugsOf(Course course) {
        return course.getTags().stream().map(Tag::getSlug).collect(Collectors.toSet());
    }

    private CourseResponse toResponse(Course course) {
        CourseResponse res = new CourseResponse();
        res.setId(course.getId());
        res.setTitle(course.getTitle());
        res.setDescription(course.getDescription());
        res.setInstructorName(course.getInstructorName());
        res.setStatus(course.getStatus());
        res.setTotalSeats(course.getTotalSeats());
        res.setStartDate(course.getStartDate());
        res.setEndDate(course.getEndDate());
        res.setTags(slugsOf(course));
        res.setCreatedAt(course.getCreatedAt());
        res.setUpdatedAt(course.getUpdatedAt());
        return res;
    }
}
