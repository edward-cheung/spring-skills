package cn.edcheung.springskills.web.validation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "cn.edcheung.*")
public class ValidationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValidationApplication.class, args);
    }

}
