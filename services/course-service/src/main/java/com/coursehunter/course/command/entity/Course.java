package com.coursehunter.course.command.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(schema = "course_inventory", name = "courses")
@Getter @Setter @NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String instructorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.DRAFT;

    @Column(nullable = false)
    private int totalSeats;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        schema = "course_inventory",
        name = "course_tags",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
