package com.vranic.zavrsnirad.util;

import com.itextpdf.io.IOException;
import com.itextpdf.text.DocumentException;
import com.vranic.zavrsnirad.model.User;
import com.vranic.zavrsnirad.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final int MAX_STACK_TRACE_ELEMENTS = 5;
    public LocalDate today = LocalDate.now();
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // ANSI escape code for red text
    public String redColor = "\u001B[31m";
    // ANSI escape code to reset color
    public String resetColor = "\u001B[0m";

    @Autowired
    private UserService userService;

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
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
            errorMessageBuilder.append(redColor)
                    .append(stackTraceElement.getClassName())
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

        //
        String errorCreatedByUser = redColor + "Error created by: " + user.getFirstName() +
                " " +
                user.getLastName() +
                "\n" +
                redColor + "Error created on: " +
                today.format(formatter) +
                "\n";

        // Print error message on console and log file
        System.err.println(errorCreatedByUser + errorMessageBuilder.toString().replace("<br>","\n") + resetColor);

        return modelAndView;
    }
}
