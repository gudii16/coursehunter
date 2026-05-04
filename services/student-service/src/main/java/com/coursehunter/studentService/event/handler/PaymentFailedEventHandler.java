package com.coursehunter.studentService.event.handler;

import com.coursehunter.studentService.entity.EnrolStatus;
import com.coursehunter.studentService.event.model.EnrollmentEvents;
import com.coursehunter.studentService.kafka.EnrollmentEventsProducer;
import com.coursehunter.studentService.repository.EnrollmentsRepository;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailedEventHandler extends ConsumedEventsHandler{


    public PaymentFailedEventHandler(EnrollmentsRepository enrollmentsRepository,
                                     EnrollmentEventsProducer enrollmentEventsProducer) {
        super(enrollmentsRepository, enrollmentEventsProducer);
    }

    @Override
    public EnrolStatus getEventType() {
        return EnrolStatus.PAYMENT_FAILED;
    }

    @Override
    public void handleEvent(EnrollmentEvents event) {
        event.setEventType(EnrolStatus.ENROLMENT_FAILED);

        updateEnrollmentStatus(event.getEnrollmentId());
        publishEnrollmentEvent(event);
    }
}
