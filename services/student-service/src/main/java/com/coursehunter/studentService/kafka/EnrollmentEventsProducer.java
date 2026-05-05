package com.coursehunter.studentService.kafka;

import com.coursehunter.studentService.event.model.EnrollmentEvents;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentEventsProducer {
    private KafkaTemplate<String, EnrollmentEvents> kafkaTemplate;
    @Value("${spring.kafka.enrollment.events.topic-name}")
    private String topicName;

    public EnrollmentEventsProducer(KafkaTemplate<String, EnrollmentEvents> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEnrollmentEvents(EnrollmentEvents event) {
        kafkaTemplate.send(topicName, String.valueOf(event.getStudentId()), event);
    }
}
