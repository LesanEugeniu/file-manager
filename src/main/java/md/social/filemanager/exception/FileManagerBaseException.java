package md.social.filemanager.exception;

public class FileManagerBaseException extends RuntimeException
{
    private int statusCode;

    private String errorType;

    public FileManagerBaseException(String message, String errorType, int statusCode)
    {
        super(message);
        this.statusCode = statusCode;
        this.errorType = errorType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorType() {
        return errorType;
    }
}
