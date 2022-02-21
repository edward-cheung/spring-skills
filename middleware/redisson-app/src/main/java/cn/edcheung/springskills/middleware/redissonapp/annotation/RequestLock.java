package cn.edcheung.springskills.middleware.redissonapp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface RequestLock {

    /**
     * key，锁对象的标识，就是上面提到的方法的某些参数。一般由方法所属类的完全限定名，方法名以及指定的参数构成，支持spel表达式。
     *
     * @return
     */
    String key() default "";

    /**
     * maximumWaiteTime，最大等待时间，避免线程死循环，单位：毫秒。
     *
     * @return
     */
    int maximumWaiteTime() default 3000;

    /**
     * expirationTime，锁的生命周期，可以有效避免因特殊原因未释放锁导致其它线程永远获取不到锁的局面，单位：毫秒。
     *
     * @return
     */
    int expirationTime() default 8000;

    /**
     * 条件，支持spel表达式
     *
     * @return
     */
    String condition() default "true";

}
