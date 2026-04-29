package com.zifang.z.config.web.config;//package com.zifang.exception;


import com.zifang.util.core.meta.BaseStatusCode;
import com.zifang.util.core.meta.Result;
import com.zifang.util.core.meta.exception.BaseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

//    @Resource
//    private List<WebExceptionHandler>  webExceptionHandlers;

    @ResponseBody
    @ExceptionHandler(value = BaseException.class)
    public Result<?> handle(BaseException e) {
        return Result.error(e.getStatusCode(), "|");
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result<?> handle(Exception e) {
        e.printStackTrace();
        return Result.error(BaseStatusCode.FAIL, e.getMessage());
    }
}
