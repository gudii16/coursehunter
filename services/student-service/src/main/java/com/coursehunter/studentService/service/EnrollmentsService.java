package com.coursehunter.studentService.service;

import com.coursehunter.studentService.entity.Enrollments;
import com.coursehunter.studentService.entity.EnrolStatus;
import com.coursehunter.studentService.kafka.EnrollmentEventsProducer;
import com.coursehunter.studentService.event.model.EnrollmentEvents;
import com.coursehunter.studentService.repository.EnrollmentsRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EnrollmentsService {
    private EnrollmentsRepository enrollmentsRepository;
    private EnrollmentEventsProducer enrollmentEventsProducer;

    public EnrollmentsService(EnrollmentsRepository enrollmentsRepository,
                              EnrollmentEventsProducer enrollmentEventsProducer) {
        this.enrollmentsRepository = enrollmentsRepository;
        this.enrollmentEventsProducer = enrollmentEventsProducer;
    }

    public void enrollStudent(int studentId, int courseId) {
        //save enrollment details in the database
        Enrollments enrollment = new Enrollments(studentId, courseId, EnrolStatus.ENROLLMENT_INITIATED);
        enrollment = enrollmentsRepository.save(enrollment);

        //Creating the enrollment initiation event
        EnrollmentEvents event = createEnrollmentInitiatedEvent(enrollment.getId(), studentId, courseId);
        // Publish the event to Kafka
        enrollmentEventsProducer.publishEnrollmentEvents(event);


    }

    private EnrollmentEvents createEnrollmentInitiatedEvent(int enrollmentId,int studentId, int courseId) {
        String eventId = UUID.randomUUID().toString();

        EnrollmentEvents event = new EnrollmentEvents();
        event.setEventId(eventId);
        event.setEventType(EnrolStatus.ENROLLMENT_INITIATED);
        event.setEnrollmentId(enrollmentId);
        event.setStudentId(studentId);
        event.setCourseId(courseId);
        event.setOccuredAt(java.time.LocalDateTime.now());

        return event;
    }


}
