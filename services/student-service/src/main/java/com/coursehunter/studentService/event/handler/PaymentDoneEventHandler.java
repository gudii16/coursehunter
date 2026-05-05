package com.coursehunter.studentService.event.handler;

import com.coursehunter.studentService.entity.EnrolStatus;
import com.coursehunter.studentService.entity.Enrollments;
import com.coursehunter.studentService.event.model.EnrollmentEvents;
import com.coursehunter.studentService.kafka.EnrollmentEventsProducer;
import com.coursehunter.studentService.repository.EnrollmentsRepository;
import org.springframework.stereotype.Component;

@Component
public class PaymentDoneEventHandler extends ConsumedEventsHandler {

    public PaymentDoneEventHandler(EnrollmentsRepository enrollmentsRepository,
                                   EnrollmentEventsProducer enrollmentEventsProducer) {
        super(enrollmentsRepository, enrollmentEventsProducer);
    }

    @Override
    public EnrolStatus getEventType() {
        return EnrolStatus.PAYMENT_DONE;
    }

    @Override
    public void handleEvent(EnrollmentEvents event) {
        updateEnrollmentStatus(event.getEnrollmentId());
    }
}
