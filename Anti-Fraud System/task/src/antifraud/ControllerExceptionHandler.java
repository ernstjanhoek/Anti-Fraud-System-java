package antifraud;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ExistsException.class)
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
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidIp() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleUserNotFound() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(LockStateException.class)
    public ResponseEntity<String> handleLockState() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}