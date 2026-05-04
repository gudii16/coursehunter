package com.coursehunter.studentService.event.model;

import com.coursehunter.studentService.entity.EnrolStatus;

import java.time.LocalDateTime;

public class EnrollmentEvents {
    private String eventId;
    private EnrolStatus eventType;
    private Integer enrollmentId;
    private Integer studentId;
    private Integer courseId;
    private LocalDateTime occuredAt;



    public EnrollmentEvents() {
    }

    public EnrollmentEvents(String eventId, Integer studentId, Integer courseId, LocalDateTime occuredAt) {
        this.eventId = eventId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.occuredAt = occuredAt;
    }

    public Integer getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Integer enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public EnrolStatus getEventType() {
        return eventType;
    }

    public void setEventType(EnrolStatus eventType) {
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public LocalDateTime getOccuredAt() {
        return occuredAt;
    }

    public void setOccuredAt(LocalDateTime occuredAt) {
        this.occuredAt = occuredAt;
    }

}
