package org.example.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDataIntegrityViolation(DataIntegrityViolationException ex, Model model) {
        model.addAttribute("errorMessage", "Нарушение ограничения целостности данных: " + ex.getMessage());
        model.addAttribute("errorType", "DataIntegrityViolationException");
        return "error";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMissingParams(MissingServletRequestParameterException ex, Model model) {
        model.addAttribute("errorMessage", "Отсутствует обязательный параметр: " + ex.getParameterName());
        model.addAttribute("errorType", "MissingServletRequestParameterException");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGlobalException(Exception ex, Model model) {
        logger.error("Произошла ошибка: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Произошла неожиданная ошибка: " + ex.getMessage());
        model.addAttribute("errorType", ex.getClass().getSimpleName());
        return "error";
    }
}