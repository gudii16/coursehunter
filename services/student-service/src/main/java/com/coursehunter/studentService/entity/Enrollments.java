package com.coursehunter.studentService.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "enrollments")
public class Enrollments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "course_id")
    private Integer courseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnrolStatus status;


    public Enrollments() {
    }

    public Enrollments(Integer studentId, Integer courseId, EnrolStatus status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public EnrolStatus getStatus() {
        return status;
    }

    public void setStatus(EnrolStatus status) {
        this.status = status;
    }
}
