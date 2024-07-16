package com.vranic.zavrsnirad.util;

import com.itextpdf.io.IOException;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final int MAX_STACK_TRACE_ELEMENTS = 5;
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        // Get the stack trace elements
        StackTraceElement[] stackTraceElements = e.getStackTrace();

        // Create a ModelAndView for the error view
        ModelAndView modelAndView = new ModelAndView("error");

        // Prepare error message with exception message
        StringBuilder errorMessageBuilder = new StringBuilder("Error occurred: ");
        errorMessageBuilder.append(e.getMessage()).append("<br><br>");

        // Append a limited number of stack trace elements to the error message
        for (int i = 0; i < Math.min(stackTraceElements.length, MAX_STACK_TRACE_ELEMENTS); i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            errorMessageBuilder.append(stackTraceElement.getClassName())
                    .append(" - ").append(stackTraceElement.getMethodName())
                    .append(" - ").append(stackTraceElement.getFileName())
                    .append(" - Line: ").append(stackTraceElement.getLineNumber())
                    .append("<br>");
        }

        // If there are more stack trace elements than MAX_STACK_TRACE_ELEMENTS, indicate that in the message
        if (stackTraceElements.length > MAX_STACK_TRACE_ELEMENTS) {
            errorMessageBuilder.append("... ").append(stackTraceElements.length - MAX_STACK_TRACE_ELEMENTS)
                    .append(" more");
        }

        // Add error message to the ModelAndView
        modelAndView.addObject("errorMessage", errorMessageBuilder.toString());

        System.err.println(errorMessageBuilder.toString().replace("<br>","\n"));

        return modelAndView;
    }
//
//    // Handler for IOException
//    @ExceptionHandler(IOException.class)
//    public ModelAndView handleIOException(IOException e) {
//        ModelAndView modelAndView = new ModelAndView("error");
//        modelAndView.addObject("errorMessage", "IO Exception occurred: " + e.getMessage());
//        return modelAndView;
//    }
//
//    // Handler for DocumentException (assuming it's a specific exception type)
//    @ExceptionHandler(DocumentException.class)
//    public ModelAndView handleDocumentException(DocumentException e) {
//        ModelAndView modelAndView = new ModelAndView("error");
//        modelAndView.addObject("errorMessage", "Document Exception occurred: " + e.getMessage());
//        return modelAndView;
//    }
//
//    @ExceptionHandler(IllegalStateException.class)
//    public ModelAndView handleIllegalStateException(IllegalStateException e) {
//        ModelAndView modelAndView = new ModelAndView("error");
//        modelAndView.addObject("errorMessage", "IllegalStateException occurred: " + e.getMessage());
//        return modelAndView;
//    }
}
