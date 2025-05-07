package exception;

import java.io.IOException;

public class TimeOverlapException extends IOException {
    public TimeOverlapException(String message) {
        super(message);
    }

}
