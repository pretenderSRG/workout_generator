package org.generator.workout.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timeStamp;
    private String error;
    private String details;

    public ErrorResponse(String error, String details) {
        this.timeStamp = LocalDateTime.now();
        this.error = error;
        this.details = details;
    }
}
