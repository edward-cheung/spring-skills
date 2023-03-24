package cn.edcheung.springskills.web.validation.exceptionHandler;

import cn.edcheung.springskills.web.validation.exception.BusinessException;
import cn.edcheung.springskills.web.validation.exception.RpcException;
import cn.edcheung.springskills.web.validation.exception.SystemException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * rpc 调用全局异常处理 aop 类
 * <p>
 * execution(public * *(..)) 定义任意公共方法的执行
 * execution(* set*(..)) 定义任何一个以"set"开始的方法的执行
 * execution(* com.xyz.service.AccountService.*(..)) 定义AccountService 接口的任意方法的执行
 * execution(* com.xyz.service.*.*(..)) 定义在service包里的任意方法的执行
 * execution(* com.xyz.service ..*.*(..)) 定义在service包和所有子包里的任意类的任意方法的执行
 * execution(* com.test.spring.aop.pointcutexp…JoinPointObjP2.*(…)) 定义在pointcutexp包和所有子包里的JoinPointObjP2类的任意方法的执行
 */
@Aspect
@Component
public class RpcGlobalExceptionAop {

    private static final Logger logger = LoggerFactory.getLogger(RpcGlobalExceptionAop.class);

    /**
     * execution(* cn.edcheung.springskills.web.validation.service ..*.*(..))：表示 rpc 接口实现类包中的所有方法
     */
    @Pointcut("execution(* cn.edcheung.springskills.web.validation.service ..*.*(..))")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object handleException(ProceedingJoinPoint joinPoint) {
        try {
            // 如果对传入对参数有修改，那么需要调用joinPoint.proceed(Object[] args)
            // 这里没有修改参数，则调用joinPoint.proceed()方法即可
            return joinPoint.proceed();
        } catch (BusinessException e) {
            // 对于业务异常，应该记录 warn 日志即可，避免无效告警
            logger.warn("全局捕获业务异常", e);
            return Result.fail(e.getCode(), e.getMessage());
        } catch (RpcException e) {
            logger.error("全局捕获第三方rpc调用异常", e);
            return Result.fail(e.getCode(), e.getMessage());
        } catch (SystemException e) {
            logger.error("全局捕获系统异常", e);
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Throwable e) {
            logger.error("全局捕获未知异常", e);
            return Result.fail(e.getMessage());
        }
    }

}
