package com.coursehunter.course.command.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateCourseRequest {
    private String title;
    private String description;
    private String instructorName;
    private Integer totalSeats;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<String> tagSlugs;
}
