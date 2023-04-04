package cn.edcheung.springskills.web.validation.mvc.advice;

import cn.edcheung.springskills.web.validation.enums.ErrorCode;
import cn.edcheung.springskills.web.validation.exception.BusinessException;
import cn.edcheung.springskills.web.validation.mvc.bean.ResultBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Description ResponseAdvice
 *
 * @author Edward Cheung
 * @date 2023/4/4
 * @since JDK 1.8
 */
@RestControllerAdvice(basePackages = {"cn.edcheung.web.validation.controller"})
public class HttpResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
        // 如果接口返回的类型本身就是ResultBean那就没有必要进行额外的操作，返回false
        return !returnType.getGenericParameterType().equals(ResultBean.class);
    }

    @Override
    public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // String类型不能直接包装，所以要进行些特别的处理
        if (returnType.getGenericParameterType().equals(String.class)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // 将数据包装在ResultBean里后，再转换为json字符串响应给前端
                return objectMapper.writeValueAsString(new ResultBean<>(ErrorCode.SUCCESS, data));
            } catch (JsonProcessingException e) {
                throw new BusinessException("返回String类型错误");
            }
        }
        // 将原本的数据包装在ResultBean里
        return new ResultBean<>(ErrorCode.SUCCESS, data);
    }
}
