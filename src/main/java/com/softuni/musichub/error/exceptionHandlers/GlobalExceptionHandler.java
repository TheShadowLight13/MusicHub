package com.softuni.musichub.error.exceptionHandlers;

import com.softuni.musichub.controller.BaseController;
import com.softuni.musichub.error.staticData.ErrorConstants;
import com.softuni.musichub.home.staticData.HomeConstants;
import com.softuni.musichub.user.exceptions.UserNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler extends BaseController {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllExceptions() {
        return this.redirect(ErrorConstants.SERVER_ERROR_ROUTE);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView handleMethodArgumentTypeMismatchException() {
        return this.redirect(HomeConstants.INDEX_ROUTE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handleHttpRequestMethodNotSupportedException() {
        return this.redirect(HomeConstants.INDEX_ROUTE);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException() {
        return this.redirect(HomeConstants.INDEX_ROUTE);
    }
}
