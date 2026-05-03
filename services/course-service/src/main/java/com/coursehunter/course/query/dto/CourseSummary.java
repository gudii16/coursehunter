package com.coursehunter.course.query.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CourseSummary {
    private UUID id;
    private String title;
    private String instructorName;
    private String status;
    private int availableSeats;
    private LocalDate startDate;
    private LocalDate endDate;
    private String[] tags;
}
