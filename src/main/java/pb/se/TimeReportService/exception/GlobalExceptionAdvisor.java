package pb.se.TimeReportService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pb.se.TimeReportService.annotation.UserNotFoundException;
@SuppressWarnings("unused")
@ControllerAdvice
public class GlobalExceptionAdvisor extends ResponseEntityExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> toResponse(UserNotFoundException e, WebRequest request) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Object> toResponse(CustomerNotFoundException e, WebRequest request) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(WorkLogNotFoundException.class)
    public ResponseEntity<Object> toResponse(WorkLogNotFoundException e, WebRequest request) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> toResponse(ForbiddenException e, WebRequest request) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFoundException(TaskNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}
