package personia.hr.exception;

public class NoEmployeeFoundException extends RuntimeException {

    public NoEmployeeFoundException() {
        super("Employee not exist");
    }
}
