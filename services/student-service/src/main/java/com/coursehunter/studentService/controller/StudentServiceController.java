package com.coursehunter.studentService.controller;

import com.coursehunter.studentService.dto.StudentDto;
import com.coursehunter.studentService.service.CreateProfileService;
import com.coursehunter.studentService.service.EnrollmentsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentServiceController {
    private CreateProfileService createProfileService;
    private EnrollmentsService enrollmentsService;

    public StudentServiceController(CreateProfileService createProfileService, EnrollmentsService enrollmentsService) {
        this.createProfileService = createProfileService;
        this.enrollmentsService = enrollmentsService;
    }

    @PostMapping("/profiles")
    public ResponseEntity<String> createProfile(@RequestBody StudentDto studentDto) {
        int createdId = createProfileService.createProfile(studentDto);
        return ResponseEntity.ok("Student profile created successfully with ID: " + createdId);
    }

    @PostMapping("/{studentId}/enrollments/{courseId}")
    public ResponseEntity<String> enrollStudent(@PathVariable int studentId, @PathVariable int courseId) {
        enrollmentsService.enrollStudent(studentId, courseId);
        return new ResponseEntity<>("Student enrollment request accepted", HttpStatus.CREATED);
    }





}
