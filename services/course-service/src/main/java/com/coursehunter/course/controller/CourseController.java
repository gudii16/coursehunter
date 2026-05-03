package com.coursehunter.course.controller;

import com.coursehunter.course.command.dto.CourseResponse;
import com.coursehunter.course.command.dto.CreateCourseRequest;
import com.coursehunter.course.command.dto.UpdateCourseRequest;
import com.coursehunter.course.command.service.CourseCommandService;
import com.coursehunter.course.query.dto.CourseSummary;
import com.coursehunter.course.query.service.CourseQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseCommandService commandService;
    private final CourseQueryService queryService;

    // ── Queries ──────────────────────────────────────────────

    @GetMapping
    public List<CourseSummary> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String search) {

        if (tag != null)    return queryService.findByTag(tag);
        if (status != null) return queryService.findByStatus(status);
        if (search != null) return queryService.search(search);
        return queryService.findAll();
    }

    @GetMapping("/{id}")
    public CourseSummary findById(@PathVariable UUID id) {
        return queryService.findById(id);
    }

    // ── Commands ─────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse create(@Valid @RequestBody CreateCourseRequest request) {
        return commandService.create(request);
    }

    @PutMapping("/{id}")
    public CourseResponse update(@PathVariable UUID id, @RequestBody UpdateCourseRequest request) {
        return commandService.update(id, request);
    }

    @PatchMapping("/{id}/publish")
    public CourseResponse publish(@PathVariable UUID id) {
        return commandService.publish(id);
    }

    @PatchMapping("/{id}/archive")
    public CourseResponse archive(@PathVariable UUID id) {
        return commandService.archive(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        commandService.delete(id);
    }
}
