package personia.hr.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvisor {

    @ExceptionHandler({MultipleRootFoundException.class, LoopHierarchyException.class, InvalidValueException.class})
    public ResponseEntity<?> handleEmployeeInputException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(NoEmployeeFoundException.class)
    public ResponseEntity<?> handleEmployeeNotFoundException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(ex.getMessage());
    }

}
