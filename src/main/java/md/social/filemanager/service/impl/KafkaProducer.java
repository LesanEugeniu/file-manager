package md.social.filemanager.service.impl;

import md.social.filemanager.dto.kafka.UserNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer
{
    private final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, UserNotification> kafkaTemplate;

    private final String topic;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, UserNotification> kafkaTemplate,@Value("${kafka.topic.notification}") String topic)
    {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendMessage(UserNotification notification)
    {
        logger.info("Sending message to [ Topic: {} ], [ Message: {} ]", topic, notification);
        kafkaTemplate.send(topic, notification);
    }
}
