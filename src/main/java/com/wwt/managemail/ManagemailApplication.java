package com.wwt.managemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.wwt.managemail.mapper")
public class ManagemailApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ManagemailApplication.class, args);
    }
    // 用于构建war文件并进行部署
    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
}
