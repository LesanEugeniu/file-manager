package md.social.filemanager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import md.social.filemanager.dto.kafka.UserNotification;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserNotificationSerializer implements Serializer<UserNotification> {
    private final Logger logger = LoggerFactory.getLogger(UserNotificationSerializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, UserNotification userNotification) {
        try {
            if (userNotification == null){
                logger.error("Message serialized in topic {} is null", topic);
                return null;
            }
            return objectMapper.writeValueAsBytes(userNotification);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing: [Topic : {}], [UserNotification : {}], [Exception : {}]"
                    ,topic , userNotification, e.getMessage());
//            throw new FileManagerBaseException();
        }
        return null;
    }
}
