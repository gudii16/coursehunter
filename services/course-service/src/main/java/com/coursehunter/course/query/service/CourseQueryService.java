package com.coursehunter.course.query.service;

import com.coursehunter.course.query.dto.CourseSummary;
import com.coursehunter.course.query.entity.CourseCatalogView;
import com.coursehunter.course.query.repository.CourseCatalogViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseQueryService {

    private final CourseCatalogViewRepository repository;

    @Transactional(readOnly = true)
    public List<CourseSummary> findAll() {
        return repository.findAll().stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public CourseSummary findById(UUID id) {
        return repository.findById(id)
                .map(this::toSummary)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<CourseSummary> findByStatus(String status) {
        return repository.findByStatus(status.toUpperCase()).stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<CourseSummary> findByTag(String tag) {
        return repository.findByTag(tag).stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<CourseSummary> search(String keyword) {
        return repository.searchByTitle(keyword).stream().map(this::toSummary).toList();
    }

    private CourseSummary toSummary(CourseCatalogView view) {
        CourseSummary summary = new CourseSummary();
        summary.setId(view.getId());
        summary.setTitle(view.getTitle());
        summary.setInstructorName(view.getInstructorName());
        summary.setStatus(view.getStatus());
        summary.setAvailableSeats(view.getAvailableSeats());
        summary.setStartDate(view.getStartDate());
        summary.setEndDate(view.getEndDate());
        summary.setTags(view.getTags());
        return summary;
    }
}
