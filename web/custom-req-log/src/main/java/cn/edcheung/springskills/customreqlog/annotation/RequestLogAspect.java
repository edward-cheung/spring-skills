package cn.edcheung.springskills.customreqlog.annotation;


import cn.edcheung.springskills.db.persistence.entity.CloudOperationLog;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * 系统操作日志记录处理
 */
@Order(1)
@Aspect
@Component
public class RequestLogAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestLogAspect.class);

    /**
     * 获取当前 HttpServletRequest 的第一种方式：自动注入HttpServletRequest对象
     */
//    @Autowired
//    private HttpServletRequest request;

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(cn.edcheung.springskills.customreqlog.annotation.RequestLog)")
    public void logPointCut() {
    }

    /**
     * 前置通知 用于拦截操作
     *
     * @param joinPoint
     */
    @AfterReturning(pointcut = "logPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        handleLog(joinPoint, null);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 连接点
     * @param e         异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfter(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e) {
        try {
            // 方法注解
            RequestLog methodAnnotationRequestLog = getMethodAnnotationLog(joinPoint);
            if (methodAnnotationRequestLog == null) {
                return;
            }
            // 类注解
            RequestLog clazzAnnotationRequestLog = getClazzAnnotationLog(joinPoint);
            // 获取当前 HttpServletRequest 的第二种方式
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            CloudOperationLog operationLog = new CloudOperationLog();
            // operationLog.setId(UUID.randomUUID().toString().replaceAll("-", "")); // 维护索引代价较大
            operationLog.setCreateDate(LocalDateTime.now());
            operationLog.setOperationEmployeeId(1L);
            operationLog.setOperationEmployeeName("超级管理员");
            String ip = ServletUtil.getClientIP(request);
            operationLog.setOperationRequestIp(ip);
            String uri = request.getRequestURI();
            operationLog.setOperationRequestUri(uri);
            // 设置方法名称(全路径)
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operationLog.setOperationMethod(className + "." + methodName + "()");
            // 设置标题模块
            if (clazzAnnotationRequestLog == null) {
                operationLog.setOperationTitle(methodAnnotationRequestLog.title());
            } else {
                String title = clazzAnnotationRequestLog.title();
                if (StrUtil.isNotBlank(title)) {
                    title += "->" + methodAnnotationRequestLog.title();
                }
                operationLog.setOperationTitle(title);
            }
            // 设置操作类别
            //operationLog.setLogType(methodAnnotationLog.logType());
            // 是否需要保存request
            if (methodAnnotationRequestLog.requestParam()) {
                String paramString = null;
                // 获取参数的信息
                Map<String, String[]> parameterMap = request.getParameterMap();
                if (CollUtil.isNotEmpty(parameterMap)) {
                    Object[] params = joinPoint.getArgs();
                    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                    Method method = signature.getMethod();

                    // 参数注解，1维是参数，2维是注解
                    Annotation[][] annotations = method.getParameterAnnotations();
                    for (int i = 0; i < annotations.length; i++) {
                        Object param = params[i];
                        Annotation[] paramAnn = annotations[i];
                        // 参数为空或者文件类型，直接下一个参数
                        if (param == null || param instanceof MultipartFile || paramAnn.length == 0) {
                            continue;
                        }
                        for (Annotation annotation : paramAnn) {
                            // 判断当前注解是否为 RequestBody.class
                            if (annotation.annotationType().equals(RequestBody.class)) {
                                // 校验该参数，验证一次退出该注解
                                paramString = JSONObject.toJSONString(params)
                                        .replaceAll("((?i)Password)\":(.*?),", "$1\":\"******\",");
                                break;
                            }
                        }
                    }
                } else {
                    Set<String> paramSet = parameterMap.keySet();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.putAll(parameterMap);
                    for (String param : paramSet) {
                        if (param.endsWith("Password")) {
                            jsonObject.put(param, new String[]{"******"});
                        }
                    }
                    paramString = jsonObject.toJSONString();
                }
                operationLog.setOperationRequestParameter(paramString);
            }
            if (e == null) {
                operationLog.setOperationRequestResult(true);
            } else {
                operationLog.setOperationRequestResult(false);
                operationLog.setOperationRequestException(ExceptionUtil.stacktraceToString(e));
            }
            if (methodAnnotationRequestLog.requestSaveDb()) {
                // 异步保存日志到数据库
                log.info("保存日志到数据库");
            }
            log.info(operationLog.toString());
        } catch (Exception ex) {
            // 记录本地异常日志
            log.error("日志记录异常：", ex);
        }
    }

    /**
     * 判断是否存在注解，如果存在就获取
     */
    private RequestLog getMethodAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(RequestLog.class);
        }
        return null;
    }

    /**
     * 判断是否类注解，如果存在就获取
     */
    private RequestLog getClazzAnnotationLog(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getTarget().getClass();
        return clazz.getAnnotation(RequestLog.class);
    }
}
