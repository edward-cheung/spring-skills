package cn.edcheung.springskills.web.validation.mvc.advice;

import cn.edcheung.springskills.web.validation.enums.ErrorCode;
import cn.edcheung.springskills.web.validation.exception.BusinessException;
import cn.edcheung.springskills.web.validation.mvc.bean.ResultBean;
import cn.edcheung.springskills.web.validation.mvc.bean.ResultBeanBuilder;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

/**
 * Description HttpGlobalExceptionHandler
 *
 * @author Edward Cheung
 * @date 2023/4/4
 * @since JDK 1.8
 */
@RestControllerAdvice("cn.edcheung.springskills.web.validation.controller")
public class HttpGlobalExceptionHandler {

    /**
     * 如果缺少参数抛出的异常是MissingServletRequestParameterException
     * 单参数校验失败后抛出的异常是ConstraintViolationException
     * get请求的对象参数校验失败后抛出的异常是BindException
     * post请求的对象参数校验失败后抛出的异常是MethodArgumentNotValidException
     * 不同异常对象的结构不同，对异常消息的提取方式也就不同
     */
    @ExceptionHandler(value = {Throwable.class})
    public ResultBean<?> handleException(Throwable e, HttpServletRequest request) {
        // 异常处理
        if (e instanceof BusinessException) {
            // 业务异常
            BusinessException businessException = (BusinessException) e;
            return ResultBeanBuilder.error(businessException.getCode(), businessException.getMessage());
        } else if (e instanceof MethodArgumentTypeMismatchException) {
            // 参数类型不匹配
            // 例如说，接口上设置了 @RequestParam("xx") 参数为 Integer，结果传递 xx 参数类型为 String
            String msg = MessageFormat.format("参数类型错误{0}", ((MethodArgumentTypeMismatchException) e).getName());
            return ResultBeanBuilder.error(ErrorCode.ERROR11003, msg);
        } else if (e instanceof MissingServletRequestParameterException) {
            // 缺少参数异常
            String msg = MessageFormat.format("缺少参数{0}", ((MissingServletRequestParameterException) e).getParameterName());
            return ResultBeanBuilder.error(ErrorCode.ERROR11003, msg);
        } else if (e instanceof ConstraintViolationException) {
            // 单个参数校验异常
            Set<ConstraintViolation<?>> sets = ((ConstraintViolationException) e).getConstraintViolations();
            if (sets != null && sets.size() > 0) {
                StringBuilder sb = new StringBuilder();
                sets.forEach(error -> {
                    if (error instanceof FieldError) {
                        sb.append(((FieldError) error).getField()).append(":");
                    }
                    sb.append(error.getMessage()).append(";");
                });
                String msg = sb.substring(0, sb.length() - 1);
                return ResultBeanBuilder.error(ErrorCode.ERROR11003, msg);
            }
            return ResultBeanBuilder.error(ErrorCode.ERROR11003);
        } else if (e instanceof MethodArgumentNotValidException) {
            // post请求的对象参数校验异常
            List<ObjectError> errors = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors();
            String msg = getValidExceptionMsg(errors);
            if (msg != null) {
                return ResultBeanBuilder.error(ErrorCode.ERROR11003, msg);
            }
            return ResultBeanBuilder.error(ErrorCode.ERROR11003);
        } else if (e instanceof BindException) {
            // get请求的对象参数校验异常
            List<ObjectError> errors = ((BindException) e).getBindingResult().getAllErrors();
            String msg = getValidExceptionMsg(errors);
            if (msg != null) {
                return ResultBeanBuilder.error(ErrorCode.ERROR11003, msg);
            }
            return ResultBeanBuilder.error(ErrorCode.ERROR11003, msg);
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            // 请求方法不支持
            // 例如说，A 接口的方法为 GET 方式，结果请求方法为 POST 方式，导致不匹配
            String msg = MessageFormat.format("不支持的请求{0}", ((HttpRequestMethodNotSupportedException) e).getMethod());
            return ResultBeanBuilder.error(ErrorCode.ERROR11003, msg);
        } else if (e instanceof NoHandlerFoundException) {
            // 请求url不存在
            // 注意，它需要设置如下两个配置项：
            // 1. spring.mvc.throw-exception-if-no-handler-found 为 true
            // 2. spring.mvc.static-path-pattern 为 /statics/**
            String msg = MessageFormat.format("不存在的请求{0}", ((NoHandlerFoundException) e).getRequestURL());
            return ResultBeanBuilder.error(ErrorCode.ERROR11003, msg);
        } else {
            // 未知异常
            return ResultBeanBuilder.error(ErrorCode.ERROR10000);
        }
    }

    private String getValidExceptionMsg(List<ObjectError> errors) {
        if (errors != null && errors.size() > 0) {
            StringBuilder sb = new StringBuilder();
            errors.forEach(error -> {
                if (error instanceof FieldError) {
                    sb.append(((FieldError) error).getField()).append(":");
                }
                sb.append(error.getDefaultMessage()).append(";");
            });
            String msg = sb.toString();
            msg = msg.substring(0, msg.length() - 1);
            return msg;
        }
        return null;
    }
}
