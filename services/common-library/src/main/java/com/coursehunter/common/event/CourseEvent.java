package com.coursehunter.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseEvent {
    private CourseEventType eventType;
    private UUID id;
    private String title;
    private String description;
    private String instructorName;
    private String status;
    private Integer totalSeats;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<String> tags;
}
