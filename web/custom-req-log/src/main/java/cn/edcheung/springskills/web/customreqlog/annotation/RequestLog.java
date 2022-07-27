package cn.edcheung.springskills.web.customreqlog.annotation;

import cn.edcheung.springskills.web.customreqlog.enums.LogTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义操作日志记录注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestLog {

    /**
     * @return 标题、模块名称
     */
    String title() default "";

    /**
     * @return 功能
     */
    LogTypeEnum logType() default LogTypeEnum.OTHER;

    /**
     * @return 请求参数
     */
    boolean requestParam() default true;

    /**
     * @return 是否需要保存数据库
     */
    boolean requestSaveDb() default true;
}

