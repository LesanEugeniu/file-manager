package md.social.filemanager.dto.kafka;

import md.social.filemanager.dto.NotificationType;

import java.time.LocalDateTime;
import java.util.Map;

public class UserNotification {
//    TODO add platform email, web...
    private String userName;
    private NotificationType notificationType;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> metadata;

    public UserNotification() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
