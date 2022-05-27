package cn.edcheung.springskills.middleware.l2cache.config;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * Description 自应用 spring cache 键名生成器
 *
 * @author Edward Cheung
 * @date 2020/11/4
 * @since JDK 1.8
 */
public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        //注意，这里不能返回null,否则会报错
        String methodName = method.getName();
        if (methodName.startsWith("get")) {
            methodName = methodName.substring(3);
        } else if (methodName.startsWith("query")) {
            methodName = methodName.substring(5);
        }
        return methodName;
    }
}
