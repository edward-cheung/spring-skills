package cn.edcheung.springskills.web.validation.mvc.config;

import cn.edcheung.springskills.web.validation.mvc.interceptor.LogInterceptor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Description WebMvcConfig
 *
 * @author Edward Cheung
 * @date 2020/11/27
 * @since JDK 1.8
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public ServletRegistrationBean<?> servletRegistrationBean(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean<DispatcherServlet> servletServletRegistrationBean = new ServletRegistrationBean<>(dispatcherServlet);
        servletServletRegistrationBean.addUrlMappings("/");
        return servletServletRegistrationBean;
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        // 允许向该服务器提交请求的URI，*表示全部允许，在SpringMVC中，如果设成*，会自动转成当前请求头中的Origin
        config.addAllowedOrigin("*");
        // 允许访问的头信息,*表示全部
        config.addAllowedHeader("*");
        // 允许提交请求的方法，*表示全部允许
        config.addAllowedMethod("*");
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("POST");
//        config.addAllowedMethod("OPTIONS");
        // 允许cookies跨域
        config.setAllowCredentials(true);
        // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * 使用此方法配置之后再使用自定义拦截器时跨域相关配置就会失效。
     * 原因是请求经过的先后顺序问题，当请求到来时会先进入拦截器中，而不是进入Mapping映射中
     * 所以返回的头信息中并没有配置的跨域信息，浏览器就会报跨域异常。
     * 正确的解决跨域问题的方法时使用CorsFilter过滤器。
     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE")
//                .allowCredentials(true)
//                .allowedHeaders("*")
//                .maxAge(3600);
//    }

    /**
     * -设置url后缀模式匹配规则
     * -该设置匹配所有的后缀
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 设置是否自动后缀路径模式匹配,即：/test/
        configurer.setUseTrailingSlashMatch(true);
        // 设置是否是后缀模式匹配,即:/test.*
        // .setUseSuffixPatternMatch(false);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        registry.addInterceptor(new LogInterceptor())
                // addPathPatterns 用于添加拦截规则，/**表示拦截所有请求
                // excludePathPatterns 用户排除拦截
                .addPathPatterns("/**");
    }
}
