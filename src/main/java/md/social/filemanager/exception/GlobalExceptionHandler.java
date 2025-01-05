package md.social.filemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{
    @ExceptionHandler(FileManagerBaseException.class)
    public ResponseEntity<ErrorDetail> handleFileManagerBaseException(FileManagerBaseException exception, WebRequest webRequest)
    {
        ErrorDetail error = new ErrorDetail(exception.getStatusCode(), exception.getErrorType(), exception.getMessage(),
                Instant.now(), webRequest.getContextPath(), webRequest.getHeader("X-Correlation-Id"));

        return new ResponseEntity<>(error, HttpStatus.valueOf(exception.getStatusCode()));
    }
}
