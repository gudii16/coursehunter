package com.coursehunter.course.command.dto;

import com.coursehunter.course.command.entity.CourseStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class CourseResponse {
    private UUID id;
    private String title;
    private String description;
    private String instructorName;
    private CourseStatus status;
    private int totalSeats;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
