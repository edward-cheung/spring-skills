package cn.edcheung.springskills.middleware.redissonapp.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Description RequestLockAspect
 *
 * @author zhangyi
 * @date 2020/4/27
 * @since JDK 1.8
 */
@Order(2)
@Aspect
@Component
public class RequestLockAspect {

    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(RequestLock)")
    public Object invoke(final ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        String targetName = point.getTarget().getClass().getName();
        Object[] arguments = point.getArgs();

        if (method != null && method.isAnnotationPresent(RequestLock.class)) {
            RequestLock requestLock = method.getAnnotation(RequestLock.class);
            LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
            String[] parameters = discoverer.getParameterNames(method);
            ExpressionParser parser = new SpelExpressionParser();
            Boolean result = judgeCondition(parser, parameters, targetName, requestLock.condition(), arguments);
            if (result) {
                final String requestLockKey = getLockKey(parser, parameters, targetName, requestLock.key(), arguments);
                RLock lock = redissonClient.getLock(requestLockKey);
                try {
                    boolean isLocked = lock.tryLock(requestLock.maximumWaiteTime(), requestLock.expirationTime(), TimeUnit.MILLISECONDS);
                    if (isLocked) {
                        return point.proceed();
                    } else {
                        throw new RuntimeException("操作频繁，请稍后再试");
                    }
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
        }
        return point.proceed();
    }

    private Boolean judgeCondition(ExpressionParser parser, String[] parameters, String targetName, String keyStr, Object[] arguments) {
        if (StringUtils.hasText(keyStr)) {
            Expression expression = parser.parseExpression(keyStr);
            EvaluationContext context = new StandardEvaluationContext();
            int length = parameters.length;
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    context.setVariable(parameters[i], arguments[i]);
                }
            }
            return expression.getValue(context, Boolean.class);
        } else {
            return Boolean.FALSE;
        }
    }

    private String getLockKey(ExpressionParser parser, String[] parameters, String targetName, String keyStr, Object[] arguments) {
        StringBuilder sb = new StringBuilder();
//		sb.append("lock.").append(targetName).append(".").append(method.getName());
        sb.append("lock.");
        if (StringUtils.hasText(keyStr)) {
            Expression expression = parser.parseExpression(keyStr);
            EvaluationContext context = new StandardEvaluationContext();
            int length = parameters.length;
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    context.setVariable(parameters[i], arguments[i]);
                }
            }
            String keysValue = expression.getValue(context, String.class);
            sb.append(keysValue);
        }
        return sb.toString();
    }
}
