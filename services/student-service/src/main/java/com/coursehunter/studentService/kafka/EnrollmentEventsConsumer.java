package com.coursehunter.studentService.kafka;

import com.coursehunter.studentService.event.model.EnrollmentEvents;
import com.coursehunter.studentService.event.router.ConsumedEventsRouter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentEventsConsumer {
    private ConsumedEventsRouter consumedEventsRouter;

    public EnrollmentEventsConsumer(ConsumedEventsRouter consumedEventsRouter) {
        this.consumedEventsRouter = consumedEventsRouter;
    }

    @KafkaListener(
            topics = "${spring.kafka.enrollment.events.topic-name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentEvent(
            @Payload EnrollmentEvents enrollmentEvents
            ) {
        consumedEventsRouter.routeEvent(enrollmentEvents);
    }


}
