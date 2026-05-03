package com.coursehunter.course.command.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(schema = "course_inventory", name = "tags")
@Getter @Setter @NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    public Tag(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
