package cn.edcheung.springskills.db.persistence.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Description MybatisConfig
 *
 * @author Edward Cheung
 * @date 2022/1/17
 * @since JDK 1.8
 */
@MapperScan(basePackages = "cn.edcheung.springskills.db.persistence.dao")
@Configuration
public class MybatisConfig {
}
