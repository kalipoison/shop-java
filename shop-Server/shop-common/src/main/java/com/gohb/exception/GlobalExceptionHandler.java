package com.gohb.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.sasl.AuthenticationException;
import java.util.List;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理全局运行时异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<String> runTimeExHandler(RuntimeException e) {
        // 处理异常
        String message = e.getMessage();
        log.error(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器维护中");
    }


    /**
     * 登录的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<String> authenticationExHandler(AuthenticationException e) {
        // 处理异常
        String message = e.getMessage();
        log.error(message);
        return ResponseEntity.badRequest().body("账号密码异常");
    }


    /**
     * 非法的参数异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentExHandler(IllegalArgumentException e) {
        // 处理异常
        String message = e.getMessage();
        log.error(message);
        return ResponseEntity.badRequest().body(message);
    }


    /**
     * 方法参数校验异常
     *
     * @param bindException
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException bindException) {
        BindingResult bindingResult = bindException.getBindingResult();
        System.out.println(bindingResult);
        StringBuilder sb = new StringBuilder("数据校验失败，原因是:");
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            sb.append("字段:" + fieldError.getField() + "," + fieldError.getDefaultMessage() + "!");
        }
        return ResponseEntity.badRequest().body(sb.toString());
    }

}