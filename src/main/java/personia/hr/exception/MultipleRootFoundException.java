package personia.hr.exception;

import java.util.List;

public class MultipleRootFoundException extends RuntimeException {

    public MultipleRootFoundException(List<String> topSupervisors) {
        super("Multiple roots found: " + String.join(", ", topSupervisors));
    }
}
