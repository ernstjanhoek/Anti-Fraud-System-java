package antifraud.AntiFraudExceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.CONFLICT)
public class ExistsException extends RuntimeException {
    public ExistsException() {
        super();
    }
}
