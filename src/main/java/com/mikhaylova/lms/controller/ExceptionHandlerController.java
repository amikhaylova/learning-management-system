package com.mikhaylova.lms.controller;

import com.mikhaylova.lms.exception.InternalServerError;
import com.mikhaylova.lms.exception.NotFoundException;
import com.mikhaylova.lms.exception.UserNotAssignedToCourseException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionHandlerController implements ErrorController {

    @ExceptionHandler
    public ModelAndView handleNotFoundException(NotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("not-found");
        modelAndView.addObject("exception", ex);
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    @ExceptionHandler
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
        ModelAndView modelAndView = new ModelAndView("access-denied");
        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        return modelAndView;
    }

    @ExceptionHandler
    public ModelAndView handleUserNotAssignedToCourseException(UserNotAssignedToCourseException ex) {
        ModelAndView modelAndView = new ModelAndView("not-assigned");
        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        return modelAndView;
    }

    @ExceptionHandler
    public ModelAndView handleInternalServerError(InternalServerError ex) {
        ModelAndView modelAndView = new ModelAndView("internal-error");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return modelAndView;
    }

    @ExceptionHandler
    public ModelAndView handleMultipartException(MultipartException ex) {
        ModelAndView modelAndView = new ModelAndView("large-payload");
        modelAndView.setStatus(HttpStatus.PAYLOAD_TOO_LARGE);
        return modelAndView;
    }

    @ExceptionHandler
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex) {
        ModelAndView modelAndView = new ModelAndView("bad-request");
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }
}
