package com.example.cloudassignment03.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonFormatException.class)
    @ResponseBody
    public String handleJsonFormatException(JsonFormatException jsonFormatException){
        return "{error: \"" + jsonFormatException.getMessage() + "\"}";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AssignmentNotFoundException.class)
    @ResponseBody
    public String handlePlanNotFoundException(AssignmentNotFoundException assignmentNotFoundException){
        return "{error: \" Assignment not found in database\"}";
    }
//
//    @ResponseStatus(HttpStatus.NOT_MODIFIED)
//    @ExceptionHandler(AssignmentNotUpdated.class)
//    public void handlePlanNotUpdatedException(AssignmentNotUpdated assignmentNotUpdated) {
//    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 409
//    @ExceptionHandler(RuntimeException.class)
//    public void handleRuntimeException(RuntimeException runtimeException) {
//        System.out.println(runtimeException.getMessage());
//
//    }

}
