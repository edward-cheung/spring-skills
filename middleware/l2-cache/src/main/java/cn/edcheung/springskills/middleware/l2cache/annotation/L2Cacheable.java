package cn.edcheung.springskills.middleware.l2cache.annotation;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 1、组合注解：被注解的注解称为组合注解。其好处在于：
 * （1）简单化注解配置，用很少的注解来标注特定含义的多个元注解
 * （2）提供了很好的扩展性，可以根据实际需要灵活的自定义注解。
 * <p>
 * 2、注解继承：@Inhberited注解可以让指定的注解在某个类上使用后，这个类的子类也将自动被该注解标记。
 * <p>
 * 3、注解的其它事项：
 * （1）当注解中含有数组属性时，使用{}赋值，各个元素使用逗号分隔。
 * （2）注解的属性可以是另外一个注解。
 * （3）注解的属性可以是另外一个注解的数组。
 * （4）注解的默认属性是value，只有一个value属性时可以不写value=xxx，直接写值即可。
 * （5）注解的属性的默认值使用default来定义。
 * <p>
 * 二级缓存：本地缓存 + 分布式缓存
 *
 * @author Edward Cheung
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Cacheable
public @interface L2Cacheable {

    @AliasFor(annotation = Cacheable.class)
    String[] value() default {};

    @AliasFor(annotation = Cacheable.class)
    String[] cacheNames() default {};

    @AliasFor(annotation = Cacheable.class)
    String key() default "";

    @AliasFor(annotation = Cacheable.class)
    String keyGenerator() default "";

    @AliasFor(annotation = Cacheable.class)
    String cacheManager() default "";

    @AliasFor(annotation = Cacheable.class)
    String cacheResolver() default "";

    @AliasFor(annotation = Cacheable.class)
    String condition() default "";

    @AliasFor(annotation = Cacheable.class)
    String unless() default "";
}
