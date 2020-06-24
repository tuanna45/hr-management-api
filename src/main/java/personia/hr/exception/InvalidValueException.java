package personia.hr.exception;

public class InvalidValueException extends RuntimeException {

    public InvalidValueException() {
        super("Input is empty");
    }

    public InvalidValueException(String employeeName) {
        super("Employee: " + employeeName + " has invalid value");
    }
}
