package com.coursehunter.course.command.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class CreateCourseRequest {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String instructorName;

    @NotNull @Min(1)
    private Integer totalSeats;

    private LocalDate startDate;
    private LocalDate endDate;
    private Set<String> tagSlugs;
}
