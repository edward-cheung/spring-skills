package cn.edcheung.springskills.web.validation.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Description JacksonConfig
 *
 * @author Edward Cheung
 * @date 2022/9/21
 * @since JDK 1.8
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // 通过该方法对mapper对象进行设置，所有序列化的对象都将按改规则进行系列化
        // Include.Include.ALWAYS 默认
        // Include.NON_DEFAULT 属性为默认值不序列化
        // Include.NON_EMPTY 属性为 空（""） 或者为 NULL 都不序列化，则返回的json是没有这个字段的。这样对移动端会更省流量
        // Include.NON_NULL 属性为NULL 不序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许出现单引号
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public String deserialize(JsonParser p, DeserializationContext ctxt)
                            throws IOException, JsonProcessingException {
                        String value = p.getValueAsString();
                        return StringUtils.isEmpty(value) ? null : value.trim();
                    }
                });
        objectMapper.registerModule(module);
        return objectMapper;
    }

    //@Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return new Jackson2ObjectMapperBuilderCustomizer() {

            @Override
            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
                jacksonObjectMapperBuilder
                        .deserializerByType(String.class, new StdScalarDeserializer<String>(String.class) {

                            private static final long serialVersionUID = 1L;

                            @Override
                            public String deserialize(JsonParser jsonParser, DeserializationContext ctx)
                                    throws IOException {
                                String value = jsonParser.getValueAsString();
                                return StringUtils.isEmpty(value) ? null : StringUtils.trimWhitespace(value);
                            }
                        });
            }
        };
    }
}

