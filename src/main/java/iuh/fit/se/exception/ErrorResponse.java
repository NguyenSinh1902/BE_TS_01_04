package iuh.fit.se.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        boolean success,
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
    public ErrorResponse(String message) {
        this(false, message, LocalDateTime.now(), null);
    }

    public ErrorResponse(String message, Map<String, String> errors) {
        this(false, message, LocalDateTime.now(), errors);
    }
}