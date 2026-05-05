package com.coursehunter.studentService.event.handler;

import com.coursehunter.studentService.entity.EnrolStatus;
import com.coursehunter.studentService.event.model.EnrollmentEvents;
import com.coursehunter.studentService.kafka.EnrollmentEventsProducer;
import com.coursehunter.studentService.repository.EnrollmentsRepository;

public abstract class ConsumedEventsHandler {
    private EnrollmentsRepository enrollmentsRepository;
    private EnrollmentEventsProducer enrollmentEventsProducer;

    public ConsumedEventsHandler(EnrollmentsRepository enrollmentsRepository,
                                 EnrollmentEventsProducer enrollmentEventsProducer) {
        this.enrollmentsRepository = enrollmentsRepository;
        this.enrollmentEventsProducer = enrollmentEventsProducer;
    }

    public void updateEnrollmentStatus(int enrollmentId) {
        // Update the enrollment status in the database
        enrollmentsRepository.findById(enrollmentId)
                .ifPresent(enrollment -> {
                    enrollmentsRepository.save(enrollment);
                });
    }

    public void publishEnrollmentEvent(EnrollmentEvents event) {
        // Publish the enrollment event to Kafka topic for notification service
        enrollmentEventsProducer.publishEnrollmentEvents(event);
    }

    public abstract EnrolStatus getEventType();
    public abstract void handleEvent(EnrollmentEvents event);

}
