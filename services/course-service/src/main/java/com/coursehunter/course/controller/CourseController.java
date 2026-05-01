package com.coursehunter.course.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @GetMapping("/hi")
    public String hi() {
        return "Hi from Course Service!";
    }
}
