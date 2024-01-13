package antifraud;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<String> handleUserExists() {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    @ExceptionHandler(RoleConflictException.class)
    public ResponseEntity<String> handleRoleConflict() {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleInvalidArgument() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
