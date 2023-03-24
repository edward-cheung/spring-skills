package cn.edcheung.springskills.web.validation.exceptionHandle;

import cn.edcheung.springskills.web.validation.bean.Result;
import cn.edcheung.springskills.web.validation.exception.BusinessException;
import cn.edcheung.springskills.web.validation.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * http 接口异常处理类
 * <p>
 * 如果是 springboot 项目，http 接口的异常处理主要分为三类：
 * 基于请求转发的方式处理异常；
 * 基于异常处理器的方式处理异常；
 * 基于过滤器的方式处理异常。
 * <p>
 * 基于请求转发的方式：真正的全局异常处理。实现方式有：
 * - BasicExceptionController
 * <p>
 * 基于异常处理器的方式：不是真正的全局异常处理，因为它处理不了过滤器等抛出的异常。实现方式有：
 * - @ExceptionHandler
 * - @ControllerAdvice + @ExceptionHandler
 * - SimpleMappingExceptionResolver
 * - HandlerExceptionResolver
 * <p>
 * 基于过滤器的方式：近似全局异常处理。它能处理过滤器及之后的环节抛出的异常。实现方式有：
 * - Filter
 */
@RestControllerAdvice("cn.edcheung.springskills.web.validation.controller")
public class HttpExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcGlobalExceptionAop.class);

    /**
     * 处理业务异常
     *
     * @param request 请求参数
     * @param e       异常
     * @return Result
     */
    @ExceptionHandler(value = BusinessException.class)
    public Object businessExceptionHandler(HttpServletRequest request, BusinessException e) {
        logger.warn("业务异常：" + e.getMessage(), e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理系统异常
     *
     * @param request 请求参数
     * @param e       异常
     * @return Result
     */
    @ExceptionHandler(value = SystemException.class)
    public Object systemExceptionHandler(HttpServletRequest request, SystemException e) {
        logger.error("系统异常：" + e.getMessage(), e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理未知异常
     *
     * @param request 请求参数
     * @param e       异常
     * @return Result
     */
    @ExceptionHandler(value = Throwable.class)
    public Object unknownExceptionHandler(HttpServletRequest request, Throwable e) {
        logger.error("未知异常：" + e.getMessage(), e);
        return Result.fail(e.getMessage());
    }

}
