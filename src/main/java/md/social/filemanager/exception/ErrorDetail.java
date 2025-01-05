package md.social.filemanager.exception;

import java.time.Instant;

public class ErrorDetail
{
    private int statusCode;

    private String errorType;

    private String message;

    private Instant timestamp;

    private String path;

    private String correlationId;

    public ErrorDetail(int statusCode, String errorType, Instant timestamp, String path) {
        this.statusCode = statusCode;
        this.errorType = errorType;
        this.timestamp = timestamp;
        this.path = path;
    }

    public ErrorDetail(int statusCode, String errorType, String message, Instant timestamp, String path, String correlationId)
    {
        this.statusCode = statusCode;
        this.errorType = errorType;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
        this.correlationId = correlationId;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public String getErrorType()
    {
        return errorType;
    }

    public void setErrorType(String errorType)
    {
        this.errorType = errorType;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getCorrelationId()
    {
        return correlationId;
    }

    public void setCorrelationId(String correlationId)
    {
        this.correlationId = correlationId;
    }
}
