package com.zifang.z.task.core.common.exception;

import com.zifang.z.task.core.common.result.Result;
import com.zifang.z.task.core.common.result.ResultCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author zifang
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 - URL: {}, Code: {}, Message: {}",
                request.getRequestURI(), e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常 - @Valid @RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败 - URL: {}, Message: {}", request.getRequestURI(), message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 参数绑定异常 - @ModelAttribute
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败 - URL: {}, Message: {}", request.getRequestURI(), message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 参数校验异常 - @RequestParam @PathVariable
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败 - URL: {}, Message: {}", request.getRequestURI(), message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 - URL: {}, Error: {}", request.getRequestURI(), e.getMessage(), e);
        return Result.fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统繁忙，请稍后再试");
    }
}
