package com.coursehunter.course.query.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "course_inventory", name = "course_catalog_view")
@Getter @Setter @NoArgsConstructor
public class CourseCatalogView {

    @Id
    private UUID id;

    private String title;
    private String description;
    private String instructorName;
    private String status;
    private int totalSeats;
    private int availableSeats;
    private LocalDate startDate;
    private LocalDate endDate;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Array(length = 100)
    private String[] tags;

    private LocalDateTime updatedAt;
}
